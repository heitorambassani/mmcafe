package com.mmcafe.upload_image_ia;

import com.mmcafe.upload_image_ia.config.RabbitTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

@SpringBootTest
@ActiveProfiles("test")
@Import(RabbitTestConfig.class) // injeta o RabbitTemplate mockado
class UploadImageIaApplicationTests {

	@Test
	void contextLoads() {
		// se só precisa subir o contexto, nada a fazer aqui
	}
}

