package com.library.bookmarker.vo;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResponseVo<T> {

    private List<T> list;
    private int totalCount;
    private int page;
    private int size;
    private int totalPages;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    public PageResponseVo(List<T> list, int totalCount, int page, int size) {
        this.list = list;
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;

        this.totalPages = (int) Math.ceil((double) totalCount / size);

        int navSize = 10;
        this.endPage = (int) (Math.ceil((double) page / navSize)) * navSize;
        this.startPage = this.endPage - (navSize - 1);

        if (this.totalPages < this.endPage) {
            this.endPage = this.totalPages == 0 ? 1 : this.totalPages;
        }

        this.prev = this.startPage > 1;
        this.next = this.endPage < this.totalPages;
    }

}
