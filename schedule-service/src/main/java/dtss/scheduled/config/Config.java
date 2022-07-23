package dtss.scheduled.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
public class Config {
    public Config0 getConfig0() {
        return config0;
    }

    final Config0 config0;
    public Config() throws IOException {
        Yaml yaml = new Yaml();
        InputStream is2 = Files.newInputStream(Paths.get("config.yaml"));
        final Map<String,Object> configMap = yaml.load(is2);
        final ObjectMapper objectMapper = new ObjectMapper();
        config0 = objectMapper.readValue(objectMapper.writeValueAsString(configMap), Config0.class);
        log.info("dtss.config.yaml=" + config0);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new Config().config0);
    }
    @Data
    public static class Config0 {
        String scheduleId;

    }
}
