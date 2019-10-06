package io.demo.storage.config;

import org.apache.commons.configuration.MapConfiguration;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.umlg.sqlg.structure.SqlgGraph;

import java.util.HashMap;
import java.util.Map;

@Profile("!test")
@Configuration
public class TinkerPopConfiguration {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Bean
    public Graph sqlgGraph() {
        Map<String, String> jdbcParams = new HashMap<>();
        jdbcParams.put("jdbc.url", databaseProperties.getUrl());
        jdbcParams.put("jdbc.username", databaseProperties.getUsername());
        jdbcParams.put("jdbc.password", databaseProperties.getPassword());
        MapConfiguration configuration = new MapConfiguration(jdbcParams);
        return SqlgGraph.open(configuration);
    }
}
