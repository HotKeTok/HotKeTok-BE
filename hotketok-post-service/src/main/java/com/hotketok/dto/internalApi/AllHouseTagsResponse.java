package com.hotketok.dto.internalApi;

import java.util.List;

public class AllHouseTagsResponse {
    private List<FloorResponse> data;

    public AllHouseTagsResponse(List<FloorResponse> data) {
        this.data = data;
    }

    public List<FloorResponse> getData() {
        return data;
    }

}