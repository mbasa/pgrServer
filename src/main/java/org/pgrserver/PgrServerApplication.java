package org.pgrserver;

import org.pgrserver.graph.MainGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@ComponentScan("org.pgrserver") 
public class PgrServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PgrServerApplication.class, args);
	}

	@Autowired
	MainGraph mainGraph;
	
	@EventListener(ApplicationReadyEvent.class)
	public void populateGraph() {
	    mainGraph.createDefaultGraph();
	}
}
