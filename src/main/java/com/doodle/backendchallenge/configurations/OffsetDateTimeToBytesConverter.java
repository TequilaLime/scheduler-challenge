package com.doodle.backendchallenge.configurations;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class OffsetDateTimeToBytesConverter implements Converter<OffsetDateTime, byte[]> {
  @Override
  public byte[] convert(final OffsetDateTime source) {
    return source.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME).getBytes();
  }
}
