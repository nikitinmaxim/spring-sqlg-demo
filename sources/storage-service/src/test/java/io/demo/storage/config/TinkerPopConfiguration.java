package io.demo.storage.config;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TinkerPopConfiguration {

    @Bean
    public Graph sqlgGraph() {
        return TinkerGraph.open();
    }
}
