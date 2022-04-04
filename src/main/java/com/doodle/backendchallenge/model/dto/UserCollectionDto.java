package com.doodle.backendchallenge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCollectionDto {
  private List<UserDto> items;
  private Long page;
  private Long pageSize;
  private Long totalSize;

  public UserCollectionDto() {}

  public UserCollectionDto(List<UserDto> items, Long page, Long pageSize, Long totalSize) {
    this.items = items;
    this.page = page;
    this.pageSize = pageSize;
    this.totalSize = totalSize;
  }

  public List<UserDto> getItems() {
    return items;
  }

  public void setItems(List<UserDto> items) {
    this.items = items;
  }

  public Long getPage() {
    return page;
  }

  public void setPage(Long page) {
    this.page = page;
  }

  public Long getPageSize() {
    return pageSize;
  }

  public void setPageSize(Long pageSize) {
    this.pageSize = pageSize;
  }

  public Long getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(Long totalSize) {
    this.totalSize = totalSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserCollectionDto that = (UserCollectionDto) o;
    return items.equals(that.items)
        && page.equals(that.page)
        && pageSize.equals(that.pageSize)
        && totalSize.equals(that.totalSize);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items, page, pageSize, totalSize);
  }

  @Override
  public String toString() {
    return "SlotCollection{"
        + "items="
        + items
        + ", page="
        + page
        + ", pageSize="
        + pageSize
        + ", totalSize="
        + totalSize
        + '}';
  }
}
