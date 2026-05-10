package br.com.cetral.centralpet.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailRecuperacaoSenha(String destino, String nome, String link) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destino);
        mensagem.setSubject("Recuperação de senha - CentralPet");
        mensagem.setText(
                "Olá, " + nome + "!\n\n" +
                "Recebemos uma solicitação para redefinir sua senha no CentralPet.\n\n" +
                "Clique no link abaixo para criar uma nova senha:\n" +
                link + "\n\n" +
                "Esse link é válido por 30 minutos.\n\n" +
                "Se você não solicitou essa recuperação, ignore este email.\n\n" +
                "CentralPet"
        );

        mailSender.send(mensagem);
    }
}