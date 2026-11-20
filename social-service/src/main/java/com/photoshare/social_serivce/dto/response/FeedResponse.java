package com.photoshare.social_serivce.dto.response;

import lombok.*;
import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedResponse {

    public List<Object> posts;
    public int page;
    public int size;
    public long totalElements;
    public int totalPages;
    public boolean last;

}
