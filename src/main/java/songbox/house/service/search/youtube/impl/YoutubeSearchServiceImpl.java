package songbox.house.service.search.youtube.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection.Response;
import org.springframework.stereotype.Service;
import songbox.house.client.YoutubeClient;
import songbox.house.domain.dto.response.SongDto;
import songbox.house.service.search.SearchQuery;
import songbox.house.service.search.youtube.YoutubeSearchService;

import java.net.URI;
import java.util.List;

import static java.lang.String.format;
import static java.net.URI.create;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Base64.getEncoder;
import static java.util.stream.Collectors.toList;
import static songbox.house.util.Constants.EMPTY_URI;
import static songbox.house.util.parser.YoutubeSearchParser.parseHtmlDocumentForSearch;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class YoutubeSearchServiceImpl implements YoutubeSearchService {

    private static final Short YOUTUBE_BITRATE = 128;

    YoutubeClient client;

    @Override
    public List<SongDto> search(SearchQuery query) throws Exception {
        Response response = client.search(query.getQuery());
        return parseHtmlDocumentForSearch(response.parse().toString())
                .stream()
                .map(youtubeSong -> new SongDto(youtubeSong.getArtist(),
                        youtubeSong.getTitle(),
                        youtubeSong.getDuration(),
                        YOUTUBE_BITRATE,
                        youtubeSong.getThumbnail(),
                        getUri(youtubeSong.getVideoId()), resourceName()))
                .collect(toList());
    }

    private URI getUri(String videoId) {
        if (videoId.isEmpty()) {
            return create("");
        }
        try {
            String base64 = new String(getEncoder().encode(videoId.getBytes()), UTF_8);
            String uriString = format("%s:%s", resourceName(), base64);
            return create(uriString);
        } catch (Exception e) {
            log.error("", e);
        }
        return EMPTY_URI;
    }
}
