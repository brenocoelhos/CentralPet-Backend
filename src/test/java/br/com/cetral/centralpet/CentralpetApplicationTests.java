package br.com.cetral.centralpet;

import br.com.cetral.centralpet.dto.CadastroPetDto;
import br.com.cetral.centralpet.model.Pet;
import br.com.cetral.centralpet.model.Usuario;
import br.com.cetral.centralpet.repository.PetRepository;
import br.com.cetral.centralpet.repository.UsuarioRepository;
import br.com.cetral.centralpet.service.CadastroPetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CentralpetApplicationTests {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private CadastroPetService cadastroPetService;

	@LocalServerPort
	private int port;

	private final HttpClient httpClient = HttpClient.newHttpClient();

	@BeforeEach
	void limparBanco() {
		petRepository.deleteAll();
		usuarioRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void buscaPetsDeveSerPublicaSemAutenticacao() throws Exception {
		HttpResponse<String> response = enviarRequest(
				HttpRequest.newBuilder(uri("/auth/busca-pets"))
						.GET()
						.build()
		);

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body()).contains("\"content\":[]");
	}

	@Test
	void buscaPetsDeveContinuarPublicaMesmoComTokenInvalido() throws Exception {
		HttpResponse<String> response = enviarRequest(
				HttpRequest.newBuilder(uri("/auth/busca-pets"))
						.header("Authorization", "Bearer token-invalido")
						.GET()
						.build()
		);

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body()).contains("\"content\":[]");
	}

	@Test
	void cadastroPetDeveExigirAutenticacao() throws Exception {
		salvarUsuario();

		HttpResponse<String> response = enviarRequest(
				HttpRequest.newBuilder(uri("/auth/cadastro-pet"))
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(petRequestJson()))
						.build()
		);

		assertThat(response.statusCode()).isBetween(400, 499);

		assertThat(petRepository.count()).isZero();
	}

	@Test
	void cadastroPetDevePersistirNovosCamposERetornaLosNaBusca() throws Exception {
		salvarUsuario();

		cadastroPetService.cadastrar(criarCadastroPetDto());

		Pet petSalvo = petRepository.findAll().getFirst();
		assertThat(petSalvo.getCastrado()).isTrue();
		assertThat(petSalvo.getVacinado()).isFalse();
		assertThat(petSalvo.getRecompensa()).isTrue();
		assertThat(petSalvo.getCep()).isEqualTo("36275000");
		assertThat(petSalvo.getLatitudeDesaparecimento()).isEqualTo(-21.028056);
		assertThat(petSalvo.getLongitudeDesaparecimento()).isEqualTo(-43.582778);

		HttpResponse<String> response = enviarRequest(
				HttpRequest.newBuilder(uri("/auth/busca-pets?usuarioId=usuario-1"))
						.GET()
						.build()
		);

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body()).contains("\"castrado\":true");
		assertThat(response.body()).contains("\"vacinado\":false");
		assertThat(response.body()).contains("\"recompensa\":true");
		assertThat(response.body()).contains("\"cep\":\"36275000\"");
		assertThat(response.body()).contains("\"latitude\":-21.028056");
		assertThat(response.body()).contains("\"longitude\":-43.582778");
	}

	@Test
	void buscaPetsDeveEncontrarPorRacaEBairro() throws Exception {
		salvarUsuario();
		cadastroPetService.cadastrar(criarCadastroPetDto());

		HttpResponse<String> porRaca = enviarRequest(
				HttpRequest.newBuilder(uri("/auth/busca-pets?termo=labra"))
						.GET()
						.build()
		);

		assertThat(porRaca.statusCode()).isEqualTo(200);
		assertThat(porRaca.body()).contains("\"nome\":\"Rex\"");
		assertThat(porRaca.body()).contains("\"raca\":\"Labrador\"");

		HttpResponse<String> porBairro = enviarRequest(
				HttpRequest.newBuilder(uri("/auth/busca-pets?termo=Flores"))
						.GET()
						.build()
		);

		assertThat(porBairro.statusCode()).isEqualTo(200);
		assertThat(porBairro.body()).contains("\"nome\":\"Rex\"");
		assertThat(porBairro.body()).contains("\"localDesaparecimento\":\"Rua das Flores, 123\"");
	}

	@Test
	void deleteCadastroPetDeveRemoverQuandoUsuarioForODono() throws Exception {
		salvarUsuario();
		cadastroPetService.cadastrar(criarCadastroPetDto());
		Long petId = petRepository.findAll().getFirst().getId();

		cadastroPetService.deletarCadastro(petId, "ana@example.com");
		assertThat(petRepository.count()).isZero();
	}

	@Test
	void deleteCadastroPetDeveRecusarQuandoUsuarioNaoForODono() throws Exception {
		salvarUsuario();
		cadastroPetService.cadastrar(criarCadastroPetDto());
		Long petId = petRepository.findAll().getFirst().getId();
		salvarUsuario("usuario-2", "bia@example.com", "12345678902", "11999999998");

		assertThatThrownBy(() -> cadastroPetService.deletarCadastro(petId, "bia@example.com"))
				.isInstanceOf(SecurityException.class)
				.hasMessageContaining("não tem permissão");

		assertThat(petRepository.count()).isEqualTo(1);
	}

	@Test
	void deleteCadastroPetDeveFalharQuandoPetNaoExistir() {
		salvarUsuario();

		assertThatThrownBy(() -> cadastroPetService.deletarCadastro(99999L, "ana@example.com"))
				.isInstanceOf(java.util.NoSuchElementException.class)
				.hasMessageContaining("Pet não encontrado");
	}

	@Test
	void deleteCadastroPetDeveExigirAutenticacao() throws Exception {
		salvarUsuario();
		cadastroPetService.cadastrar(criarCadastroPetDto());
		Long petId = petRepository.findAll().getFirst().getId();

		HttpResponse<String> response = enviarRequest(
				HttpRequest.newBuilder(uri("/auth/cadastro-pet/" + petId))
						.DELETE()
						.build()
		);

		assertThat(response.statusCode()).isBetween(400, 499);
		assertThat(petRepository.count()).isEqualTo(1);
	}

	private URI uri(String path) {
		return URI.create("http://localhost:" + port + path);
	}

	private HttpResponse<String> enviarRequest(HttpRequest request) throws IOException, InterruptedException {
		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private Usuario salvarUsuario() {
		return salvarUsuario("usuario-1", "ana@example.com", "12345678901", "11999999999");
	}

	private Usuario salvarUsuario(String id, String email, String cpf, String telefone) {
		Usuario usuario = new Usuario();
		usuario.setId(id);
		usuario.setNome("Usuário Teste");
		usuario.setSenha("senha-criptografada");
		usuario.setEmail(email);
		usuario.setCpf(cpf);
		usuario.setDataNascimento(LocalDate.of(1995, 5, 10));
		usuario.setTelefone(telefone);
		usuario.setRua("Rua Teste, 123");
		usuario.setNumero("123");
		usuario.setCidade("Sao Paulo");
		usuario.setEstado("SP");
		return usuarioRepository.save(usuario);
	}

	private CadastroPetDto criarCadastroPetDto() {
		CadastroPetDto dto = new CadastroPetDto();
		dto.setUsuarioId("usuario-1");
		dto.setNome("Rex");
		dto.setEspecie("Cachorro");
		dto.setRaca("Labrador");
		dto.setCor("Amarelo");
		dto.setPorte("Grande");
		dto.setDataDesaparecimento(LocalDate.of(2026, 4, 20));
		dto.setLocalDesaparecimento("Rua das Flores, 123");
		dto.setCep("36275000");
		dto.setLatitude(-21.028056);
		dto.setLongitude(-43.582778);
		dto.setDescricao(java.util.List.of("Coleira vermelha", "Mancha na pata"));
		dto.setCastrado(true);
		dto.setVacinado(false);
		dto.setRecompensa(true);
		dto.setFotoUrl("file:///...");
		dto.setNomeTutor("João Silva");
		dto.setTelefoneTutor("11999998888");
		return dto;
	}

	private String petRequestJson() {
		return """
				{
				  "usuarioId": "usuario-1",
				  "nome": "Thor",
				  "especie": "Cachorro",
				  "raca": "Vira-lata",
				  "cor": "Caramelo",
				  "porte": "Médio",
				  "dataDesaparecimento": "2026-04-15",
				  "localDesaparecimento": "Praça Central",
				  "descricao": ["Muito dócil", "Usa coleira azul"],
				  "castrado": true,
				  "vacinado": false,
				  "recompensa": true,
				  "fotoUrl": "https://example.com/thor.jpg",
				  "nomeTutor": "Maria",
				  "telefoneTutor": "11988887777"
				}
				""";
	}

}
