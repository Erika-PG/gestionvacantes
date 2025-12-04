package com.erika.gestionvacantes.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    /**
     * Notificar a aspirante que su solicitud fue ACEPTADA
     */
    public boolean notificarSolicitudAceptada(String emailAspirante, String nombreAspirante,
                                              String tituloVacante, String empresaEmpleador) {
        String asunto = "¡Felicidades! Tu solicitud fue aceptada - " + tituloVacante;
        String contenidoHTML = construirEmailSolicitudAceptada(nombreAspirante, tituloVacante, empresaEmpleador);

        return enviarEmailConSendGrid(emailAspirante, asunto, contenidoHTML);
    }

    /**
     * Notificar a aspirante que su solicitud fue RECHAZADA
     */
    public boolean notificarSolicitudRechazada(String emailAspirante, String nombreAspirante,
                                               String tituloVacante, String empresaEmpleador) {
        String asunto = "Actualizacion sobre tu solicitud - " + tituloVacante;
        String contenidoHTML = construirEmailSolicitudRechazada(nombreAspirante, tituloVacante, empresaEmpleador);

        return enviarEmailConSendGrid(emailAspirante, asunto, contenidoHTML);
    }

    /**
     * Enviar email usando SendGrid API
     */
    private boolean enviarEmailConSendGrid(String destinatario, String asunto, String contenidoHTML) {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(destinatario);
        Content content = new Content("text/html", contenidoHTML);
        Mail mail = new Mail(from, asunto, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("✅ Email enviado exitosamente via SendGrid a: " + destinatario);
                System.out.println("   Status Code: " + response.getStatusCode());
                return true;
            } else {
                System.err.println("❌ Error SendGrid: " + response.getStatusCode());
                System.err.println("   Response: " + response.getBody());
                return false;
            }
        } catch (IOException e) {
            System.err.println("❌ Excepcion al enviar email via SendGrid: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Construir contenido HTML para solicitud aceptada
     */
    private String construirEmailSolicitudAceptada(String nombreAspirante, String tituloVacante, String empresa) {
        return "<!DOCTYPE html>" +
                "<html><head><style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #28a745; color: white; padding: 20px; text-align: center; }" +
                ".content { padding: 20px; background-color: #f9f9f9; }" +
                ".footer { text-align: center; padding: 10px; color: #777; font-size: 12px; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'><h1>¡Felicidades!</h1></div>" +
                "<div class='content'>" +
                "<p>Hola <strong>" + nombreAspirante + "</strong>,</p>" +
                "<p>Nos complace informarte que tu solicitud para la vacante <strong>" + tituloVacante + "</strong> " +
                "en <strong>" + empresa + "</strong> ha sido <span style='color:#28a745; font-weight:bold;'>ACEPTADA</span>.</p>" +
                "<p>Pronto la empresa se pondra en contacto contigo para continuar con el proceso.</p>" +
                "<p>¡Te deseamos mucho exito!</p>" +
                "</div>" +
                "<div class='footer'>Sistema de Gestion de Vacantes</div>" +
                "</div></body></html>";
    }

    /**
     * Construir contenido HTML para solicitud rechazada
     */
    private String construirEmailSolicitudRechazada(String nombreAspirante, String tituloVacante, String empresa) {
        return "<!DOCTYPE html>" +
                "<html><head><style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #dc3545; color: white; padding: 20px; text-align: center; }" +
                ".content { padding: 20px; background-color: #f9f9f9; }" +
                ".footer { text-align: center; padding: 10px; color: #777; font-size: 12px; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'><h2>Actualizacion de Solicitud</h2></div>" +
                "<div class='content'>" +
                "<p>Hola <strong>" + nombreAspirante + "</strong>,</p>" +
                "<p>Lamentamos informarte que tu solicitud para la vacante <strong>" + tituloVacante + "</strong> " +
                "en <strong>" + empresa + "</strong> no fue seleccionada en esta ocasion.</p>" +
                "<p>Te animamos a seguir buscando oportunidades en nuestra plataforma. Cada experiencia es un paso mas hacia tu objetivo.</p>" +
                "<p>¡Mucho animo!</p>" +
                "</div>" +
                "<div class='footer'>Sistema de Gestion de Vacantes</div>" +
                "</div></body></html>";
    }
}
