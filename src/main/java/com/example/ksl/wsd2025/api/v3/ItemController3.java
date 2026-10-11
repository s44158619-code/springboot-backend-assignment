
package com.example.ksl.wsd2025.api.v3;

import com.example.ksl.wsd2025.api.dto.ItemDto;
import com.example.ksl.wsd2025.api.request.ItemCreateRequest;
import com.example.ksl.wsd2025.api.request.ItemUpdateRequest;
import com.example.ksl.wsd2025.api.response.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v3/items")
public class ItemController3 {

    // 메모리에 상품 정보를 저장하는 임시 DB
    private final Map<Long, ItemDto> store = new HashMap<>();
    private long sequence = 1L;

    // GET 1: 전체 상품 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemDto>>> getItems() {
        List<ItemDto> items = new ArrayList<>(store.values());

        return ResponseEntity.ok(
                ApiResponse.success(items)
        );
    }

    // GET 2: 특정 상품 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@PathVariable Long id) {
        ItemDto item = store.get(id);

        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("상품을 찾을 수 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(item));
    }

    // GET 3: 상품 이름 검색 및 페이징
    @GetMapping("/search")
    public ResponseEntity<?> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                            "page는 0 이상, size는 1 이상이어야 합니다."
                    ));
        }

        List<ItemDto> result = store.values().stream()
                .filter(item -> keyword == null
                        || (item.getName() != null
                        && item.getName().contains(keyword)))
                .skip((long) page * size)
                .limit(size)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // POST 1: 상품 등록
    @PostMapping
    public ResponseEntity<?> createItem(
            @RequestBody ItemCreateRequest request
    ) {
        if (!isValidItem(request.getName(), request.getPrice())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                            "상품 이름과 0 이상의 가격을 입력해주세요."
                    ));
        }

        ItemDto item = new ItemDto();
        item.setId(sequence++);
        item.setName(request.getName());
        item.setPrice(request.getPrice());

        store.put(item.getId(), item);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(item));
    }

    // POST 2: 헤더를 포함한 상품 등록
    @PostMapping("/with-header")
    public ResponseEntity<?> createItemWithHeader(
            @RequestBody ItemCreateRequest request,
            @RequestHeader(
                    value = "X-USER-ID",
                    required = false
            ) String userId,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {
        if (!isValidItem(request.getName(), request.getPrice())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                            "상품 이름과 0 이상의 가격을 입력해주세요."
                    ));
        }

        ItemDto item = new ItemDto();
        item.setId(sequence++);
        item.setName(request.getName());
        item.setPrice(request.getPrice());

        store.put(item.getId(), item);

        Map<String, Object> result = new HashMap<>();
        result.put("item", item);
        result.put("userId", userId);
        result.put("hasAuthorization",
                authorization != null && !authorization.isBlank());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    // PUT 1: 상품 이름 및 가격 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long id,
            @RequestBody ItemUpdateRequest request
    ) {
        ItemDto item = store.get(id);

        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("상품을 찾을 수 없습니다."));
        }

        if (request.getName() == null
                && request.getPrice() == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                            "수정할 이름 또는 가격을 입력해주세요."
                    ));
        }

        if (request.getName() != null) {
            if (request.getName().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(
                                "상품 이름은 비워둘 수 없습니다."
                        ));
            }
            item.setName(request.getName());
        }

        if (request.getPrice() != null) {
            if (request.getPrice() < 0) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(
                                "가격은 0 이상이어야 합니다."
                        ));
            }
            item.setPrice(request.getPrice());
        }

        return ResponseEntity.ok(ApiResponse.success(item));
    }

    // PUT 2: 상품 가격만 수정
    @PutMapping("/{id}/price")
    public ResponseEntity<?> updatePrice(
            @PathVariable Long id,
            @RequestBody ItemUpdateRequest request
    ) {
        ItemDto item = store.get(id);

        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("상품을 찾을 수 없습니다."));
        }

        if (request.getPrice() == null
                || request.getPrice() < 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                            "0 이상의 가격을 입력해주세요."
                    ));
        }

        item.setPrice(request.getPrice());

        return ResponseEntity.ok(ApiResponse.success(item));
    }

    // DELETE 1: 특정 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        ItemDto removed = store.remove(id);

        if (removed == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("상품을 찾을 수 없습니다."));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("deletedId", id);
        result.put("message", "상품이 삭제되었습니다.");

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // DELETE 2: 전체 상품 삭제
    @DeleteMapping
    public ResponseEntity<?> deleteAllItems() {
        int deletedCount = store.size();
        store.clear();

        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", deletedCount);
        result.put("message", "전체 상품이 삭제되었습니다.");

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 상품 입력값 검증용 메소드
    private boolean isValidItem(String name, Integer price) {
        return name != null
                && !name.isBlank()
                && price != null
                && price >= 0;
    }
}
