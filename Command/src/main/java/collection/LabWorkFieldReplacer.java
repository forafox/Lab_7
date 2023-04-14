package collection;

import java.io.PrintStream;
import java.util.Locale;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 04.04.2023 21:21
 */
public class LabWorkFieldReplacer {
    public static LabWork update(LabWork source, String[] field, String[] value, PrintStream printStream) throws IllegalArgumentException { //req-element = field[i] , value[i]
        if (field.length != value.length) throw new IllegalArgumentException();
        for (int i = 0; i < field.length; i++) {
            if (LabWorkFieldValidation.validate(field[i], value[i])) {
                switch (field[i]) {
                    case "name": {
                        source.setName(value[i]);
                        break;
                    }
                    case "coordinate_x": {
                        source.setCoordinateX(Long.parseLong(value[i]));
                        break;
                    }
                    case "coordinate_y": {
                        source.setCoordinateY(Double.parseDouble(value[i]));
                        break;
                    }
                    case "minimalPoint": {
//                        if (value[i].equals("")) {
//                            source.setAge(null);
//                        } else {
//                            source.setAge(Long.parseLong(value[i]));
//                        }
                        source.setMinimalPoint(Integer.parseInt(value[i]));
                        break;
                    }
                    case "maximumPoint": {
                        source.setMaximumPoint(Double.parseDouble(value[i]));
                        break;
                    }
                    case "personalQualitiesMaximum": {
                        source.setPersonalQualitiesMaximum(Integer.parseInt(value[i]));
                        break;
                    }
                    case "difficulty": {
                        source.setDifficulty(Difficulty.valueOf(value[i].toUpperCase(Locale.ROOT)));
                        break;
                    }
                    case "PersonName": {
                        source.getPerson().setName(value[i]);
                        break;
                    }
                    case "PersonHeight": {
                        source.getPerson().setHeight(Float.parseFloat(value[i]));
                        break;
                    }
                    case "PersonPassportID": {
                        source.getPerson().setPassportID(value[i]);
                        break;
                    }
                    case "LocationX": {
                        source.getPerson().getLocation().setX(Integer.parseInt(value[i]));
                        break;
                    }
                    case "LocationY": {
                        source.getPerson().getLocation().setY(Float.parseFloat(value[i]));
                        break;
                    }
                    case "LocationName": {
                        source.getPerson().getLocation().setName(value[i]);
                        break;
                    }
                }
            }
        }
        printStream.println("Значение указанных полей было изменено.");
        return source;
    }
}
