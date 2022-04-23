package org.caching.server.rcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RemoteCachingServer {

	public static void main(String[] args) {
		SpringApplication.run(RemoteCachingServer.class, args);
	}

}
