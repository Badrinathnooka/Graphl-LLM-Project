package demo;


import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@ReadingConverter
@Component
public class StringToIntegerConverter implements Converter<String, Integer> {
    @Override
    public Integer convert(String source) {
        if (source == null || source.isBlank() || "NA".equalsIgnoreCase(source)) {
            return null;
        }
        try {
            return Integer.valueOf(source);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
