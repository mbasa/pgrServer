package org.pgrserver;

import javax.annotation.PostConstruct;

import org.pgrserver.graph.MainGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.pgrserver") 
public class PgrServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PgrServerApplication.class, args);
	}

	@Autowired
	MainGraph mainGraph;
	
	@PostConstruct
	private void populateGraph() {
	    mainGraph.createDefaultGraph();
	}
}
