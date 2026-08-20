package com.library.bookmarker.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bookmarker.mapper.LibraryMapper;
import com.library.bookmarker.vo.LibraryVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@SpringBootTest
public class LibraryInsertTest {

    @Autowired
    private LibraryMapper libraryMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void 도서관_API_등록_테스트() {
        String key = "4c4758f05d057576af76b56cc7085a08e008d8b16bbbf407b7aaa39684358801";
        List<LibraryVo> libraryVoList = new ArrayList<>();

        try {
            // api 대상 주소
            String url = "https://data4library.kr/api/libSrch?authKey="+ key +"&pageNo=1&pageSize=1599&format=json";
            // json 전체 객체를 map 구조로 파싱
            Map<String, Object> responseData = objectMapper.readValue(new URL(url), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> responseContainer = (Map<String, Object>) responseData.get("response");

            // libs 대상 리스트 맵으로 셋업
            List<Map<String, Object>> libsList = (List<Map<String, Object>>) responseContainer.get("libs");

            // 리스트 갯수만큼 루프 돌면서 필요 데이터 매핑
            for (Map<String, Object> libContainer : libsList) {
                Map<String, Object> libMap = (Map<String, Object>) libContainer.get("lib");
                if (libMap == null) continue;

                String address = String.valueOf(libMap.get("address"));
                String[] parsedAddrs = parsingToAddress(address);

                LibraryVo libraryVo = LibraryVo.builder()
                        .libCode(String.valueOf(libMap.get("libCode")))
                        .libName(String.valueOf(libMap.get("libName")))
                        .libData(objectMapper.writeValueAsString(libContainer.get("lib")))
                        .latitude(String.valueOf(libMap.get("latitude")))
                        .longitude(String.valueOf(libMap.get("longitude")))
                        .libSido(parsedAddrs[0])
                        .libSigungu(parsedAddrs[1])
                        .build();

                libraryVoList.add(libraryVo);
            }

            if (!libraryVoList.isEmpty()) {
                int totalSize = libraryVoList.size();
                int chunkSize = 300;

                // 300개씩 나눠 저장
                for (int i = 0; i < totalSize; i += chunkSize) {
                    int endIndex = Math.min(i + chunkSize, totalSize);
                    List<LibraryVo> subList = libraryVoList.subList(i, endIndex);

                    log.info("데이터 vo 매핑 확인", libraryVoList);
                    int result = libraryMapper.insertLibraryList(libraryVoList);
                    log.info("데이터 등록 성공", result);
                }
            }

            } catch (Exception e) {
                log.error("적재중 에러 발생", e);
        }
    }

    // 주소 가공 띄어쓰기 기준으로 시/도, 시/군/구 분리
    public String[] parsingToAddress(String address) {
        if (address == null || address.isBlank()) {
            return new String[]{"", ""};
        }

        String[] addressParts = address.split(" ", 2);
        String sido = addressParts[0];
        String sigungu = "";

        if (addressParts.length > 1) {
            String remainAddress = addressParts[1];

            // 첫 띄어쓰기 이후 가장 먼저 나오는 시/군/구 단어 정규식
            Pattern pattern = Pattern.compile("[가-힣]+([시|군|구])");
            Matcher matcher = pattern.matcher(remainAddress);

            if (matcher.find()) {
                sigungu = matcher.group(); // 정규식에 걸러진 텍스트 매핑
            }
        }
        return new String[]{sido, sigungu};
    }
}
