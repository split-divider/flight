package songbox.house.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import songbox.house.domain.dto.response.vk.VkSearchResponseDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static songbox.house.util.Constants.JSON_DELIMITER;

@Component
@Slf4j
//TODO refactor
public class JsonUtils implements ApplicationContextAware {

    private static ObjectMapper mapper = new ObjectMapper();
    private static Pattern searchFromNewsFeedPattern = compile("data-audio=\"(\\[.*?])\"");

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        mapper = applicationContext.getBean(ObjectMapper.class)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @SneakyThrows
    public static String toString(final Object obj) {
        return mapper.writeValueAsString(obj);
    }

    @SneakyThrows
    public static <T> T fromString(final String json, final Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.debug("Error parsing json {} to DTO {}", json, clazz);
        }

        return null;
    }

    @SneakyThrows
    public static <T> List<T> fromString(final String json, final String keyName, final Class<T> clazz) {
        try {
            JsonNode jsonNode = mapper.readTree(json).get(keyName);

            return stream(jsonNode.spliterator(), false)
                    .map(node -> readValue(node, clazz))
                    .collect(toList());
        } catch (Exception e) {
            log.debug("Error parsing json {} to DTO {}", json, clazz);
        }

        return emptyList();
    }

    public static VkSearchResponseDto searchFromNewsFeedResponseToObject(final String body) {
        Matcher matcher = searchFromNewsFeedPattern.matcher(body);
        VkSearchResponseDto vkSearchResponseDto = new VkSearchResponseDto();
        List<ArrayList> list = new ArrayList<>();
        while (matcher.find()) {
            ArrayList obj = fromString(matcher.group(1).replaceAll("&quot;", "\""), ArrayList.class);
            list.add(obj);
        }

        vkSearchResponseDto.setList(list);
        return vkSearchResponseDto;
    }

    public static <T> T responseToObject(final String body, final Class<T> clazz) {
        String json = body.substring(body.indexOf(JSON_DELIMITER) + JSON_DELIMITER.length());
        json = json.substring(0, json.indexOf("<!>"));
        return fromString(json, clazz);
    }

    private static <T> T readValue(JsonNode node, Class<T> clazz) {
        try {
            return mapper.treeToValue(node, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
