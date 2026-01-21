package com.newlog.backend.controller.mail;

import com.newlog.backend.dto.mail.PasswordMailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailService {

    private JavaMailSender javaMailSender;
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    // 비밀번호 변경 이메일 발송
    public void passwordEmailSend(PasswordMailDto passwordMailDto, String password) {
        try {

            log.info("비밀번호 재설정 이메일 발송 시작: email={}", passwordMailDto.getAddress());

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(passwordMailDto.getAddress());
            helper.setSubject("NEWSLOG 에서 보낸 새로운 비밀번호입니다.");

            String content = String.format("""
                <!DOCTYPE html>
                <html>
                <body>
                <div style="margin:100px;">
                    <h1>변경된 비밀번호는 다음과 같습니다.</h1>
                    <br>
                    <div align="center" style="border:1px solid black; padding:20px;">
                        <h3>%s</h3>
                    </div>
                    <br/>
                </div>
                </body>
                </html>
                """, password);

            helper.setText(content, true);
            javaMailSender.send(message);

            log.info("비밀번호 재설정 이메일 발송 완료: email={}", passwordMailDto.getAddress());

        } catch (MessagingException e) {
            log.error("비밀번호 재설정 이메일 발송 실패: email={}, error={}", passwordMailDto, e.getMessage(), e);
            throw new IllegalArgumentException("이메일 전송에 실패했습니다.", e);
        }
    }
}
