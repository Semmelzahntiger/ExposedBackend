package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.service.stream.TikTokStreamingService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/api/stream")
@Slf4j
public class StreamController {
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .build();
    private final TikTokStreamingService tikTokStreamingService;

    public StreamController(TikTokStreamingService tikTokStreamingService) {
        this.tikTokStreamingService = tikTokStreamingService;
    }

    @GetMapping("/tiktok/{roomId}/{postId}")
    public ResponseEntity<StreamingResponseBody> stream(
            @PathVariable String roomId,
            @PathVariable String postId,
            @RequestHeader(value = "Range", required = false) String range
    ) throws IOException {
         TikTokStreamingService.StreamData streamData = tikTokStreamingService.getStreamDataFor(roomId, postId);
         if(streamData == null) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
         }
        Request.Builder builder = new Request.Builder()
                .url(streamData.cdn())
                .header("Referer", "https://www.tiktok.com/")
                .header("Cookie", "tt_chain_token="+ streamData.ttChainCookie());
        if(range != null) builder.header("Range", range);
        Request req = builder.build();

        Response stream = httpClient.newCall(req).execute();
        if(stream.code() == HttpServletResponse.SC_FORBIDDEN || stream.code() == HttpServletResponse.SC_UNAUTHORIZED) {
            stream.close();
            return ResponseEntity.status(stream.code()).build();
        }
        if(!stream.isSuccessful()) {
            stream.close();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .header("X-Proxy-Error", "upstream " + postId)
                    .build();
        }
        HttpHeaders out = new HttpHeaders();
        copyHeadersIfPresent(stream, out, "Content-Type", "Content-Length", "Content-Range", "Accept-Ranges");

        StreamingResponseBody body = outputStream -> {
            try(Response response = stream; InputStream in = response.body().byteStream()) {
                in.transferTo(outputStream);
            } catch (IOException e) {}
        };
        return ResponseEntity
                .status(stream.code())
                .headers(out)
                .body(body);

    }
    private static void copyHeadersIfPresent(Response upstream, HttpHeaders out, String... names) {
        for (String name : names) {
            String value = upstream.header(name);
            if (value != null) {
                out.set(name, value);
            }
        }
    }
}
