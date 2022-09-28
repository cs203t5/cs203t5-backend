package com.example.Vox.Viridis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.Vox.Viridis.security.RsaKeyProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class VoxViridisApplication {
	public static void main(String[] args) {
		SpringApplication.run(VoxViridisApplication.class, args);
	}

}
