package ro.sync.json.instance.generator;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import org.everit.json.schema.*;
import org.everit.json.schema.CombinedSchema.ValidationCriterion;
import org.everit.json.schema.regexp.Regexp;
import org.w3c.dom.css.ElementCSSInlineStyle;

import com.mifmif.common.regex.Generex;

public class JsonGeneratorEngine {
	
	StringBuilder builder = new StringBuilder();
	private JsonGeneratorOptions options;
	
	private String lastProperty;
	
	public JsonGeneratorEngine(JsonGeneratorOptions options) {
		this.options = options;
	}

	public String generateRandomString(int length) {
    	final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(ALPHABET.charAt(new Random().nextInt(ALPHABET.length())));
        }
        return stringBuilder.toString();
    }
	
    void visit(Schema schema) {
    	System.out.println("VISITED SCHEMA: " + schema.getClass());
    	if (schema instanceof ObjectSchema) {
    		visitObjectSchema((ObjectSchema) schema);
    	} else if (schema instanceof NumberSchema) {
    		visitNumberSchema((NumberSchema) schema);
    	} else if (schema instanceof ReferenceSchema) {
    		visitReferenceSchema((ReferenceSchema) schema);
    	} else if (schema instanceof StringSchema) {
    		visitStringSchema((StringSchema) schema);
    	} else if (schema instanceof ArraySchema) {
    		visitArraySchema((ArraySchema) schema);
    	} else if (schema instanceof EnumSchema) {
    		visitEnumSchema((EnumSchema) schema);
    	} else if (schema instanceof BooleanSchema) {
    		visitBooleanSchema((BooleanSchema) schema);
    	} else if (schema instanceof CombinedSchema) {
    		visitCombinedSchema((CombinedSchema) schema);
    	} 
    	// TODO Handle all schemas.
    }
    /**
     * 
     * @param objectSchema schema to visit
     */
    private void visitObjectSchema(ObjectSchema objectSchema) {
    	builder.append("{");
        visitPropertyNameSchema(objectSchema.getPropertyNameSchema());
        for (Map.Entry<String, Set<String>> entry : objectSchema.getPropertyDependencies().entrySet()) {
            visitPropertyDependencies(entry.getKey(), entry.getValue());
        }
        visitAdditionalProperties(objectSchema.permitsAdditionalProperties());
        visitSchemaOfAdditionalProperties(objectSchema.getSchemaOfAdditionalProperties());
        
        for (Map.Entry<String, Schema> schemaDep : objectSchema.getSchemaDependencies().entrySet()) {
            visitSchemaDependency(schemaDep.getKey(), schemaDep.getValue());
        }
        Map<String, Schema> propertySchemas = objectSchema.getPropertySchemas();
        if (propertySchemas != null) {
            for (Map.Entry<String, Schema> entry : propertySchemas.entrySet()) {
            	if (objectSchema.getRequiredProperties().contains(entry.getKey()) || objectSchema.getRequiredProperties().isEmpty()) {
            		visitPropertySchema(entry.getKey(), entry.getValue());
            	}
            }
        }
        builder.setLength(builder.length()-1); // remove the last comma
        builder.append("}");
    }
    /**
     * Appends an integer value between minimum and maximum. If minimum and maximum are not defined, they are assigned
     * values 0 and 100, respectively. If multipleOf is defined, then the integer value will be a multiple of that number.
     * @param numberSchema schema to visit
     */
    void visitNumberSchema(NumberSchema numberSchema) {
        System.out.println(numberSchema.getMultipleOf());
        
        int minimum = (numberSchema.getMinimum() != null) ? numberSchema.getMinimum().intValue() : 0;
        int maximum = (numberSchema.getMaximum() != null) ? numberSchema.getMaximum().intValue() : 100;
        
        int exMinimum = (numberSchema.isExclusiveMinimum() ? 1:0 );
        int exMaximum = (numberSchema.isExclusiveMaximum() ? 1:0 );
        
        int isMultipleOf = numberSchema.getMultipleOf() != null ? 1 : 0;
        int multipleOf = isMultipleOf == 1 ? numberSchema.getMultipleOf().intValue() : 1;
        
        int lowerLimit = (minimum + exMinimum);
        int upperLimit = (maximum + exMaximum);
        
        if (options.isGenerateRandomValues()) {
        	builder.append(" ").append(new Random().nextInt(upperLimit) + lowerLimit/ multipleOf * multipleOf);
        } else {
        	builder.append(" ").append((lowerLimit/ multipleOf + isMultipleOf) * multipleOf);
        }
    }
    
    /**
     * Appends a random number of items if the option is random, or 2 items by default. The items use the lastPropertyName 
     * as they have the same name + an increasing index.
     * @param arraySchema schema to visit
     */
    void visitArraySchema(ArraySchema arraySchema) {
    	// get minimum and maximum number of items the schema can contain
        int minItems = arraySchema.getMinItems() != null ? arraySchema.getMinItems().intValue() : 2;
        int maxItems = arraySchema.getMaxItems() != null ? arraySchema.getMaxItems().intValue() : 10;
        
        String lastPropertyName = lastProperty;
        int numberOfItems = options.isGenerateRandomValues() ? new Random().nextInt(maxItems) + minItems : minItems;
        
        visitUniqueItems(arraySchema.needsUniqueItems());
        builder.append("[");
        
        if(arraySchema.getAllItemSchema() != null) {
        	for (int i = 0; i < numberOfItems; i++) {
        		lastProperty = lastPropertyName + i;
            	visit(arraySchema.getAllItemSchema());
            	if (i < numberOfItems - 1) {
            		builder.append(",");
            	}
            }
        } else if (arraySchema.getItemSchemas() != null) {
        	List<Schema> itemSchemas = arraySchema.getItemSchemas();
        	for (int i = 0; i < itemSchemas.size(); ++i) {
        		visit(itemSchemas.get(i));
        		builder.append(",");
        	}
        }
        if (builder.charAt(builder.length() - 1) == ',') {
        	builder.setLength(builder.length()-1);
        }
        builder.append("]");
   
        visitSchemaOfAdditionalItems(arraySchema.getSchemaOfAdditionalItems());
        //visitContainedItemSchema(arraySchema.getContainedItemSchema());
    }
    /**
     * Appends a random string element if the option is random, or the name of the property otherwise.
     * @param stringSchema schema to visit
     */
    void visitStringSchema(StringSchema stringSchema) {
    	// get minimum and maximum length the schema can have
        int minLenght = stringSchema.getMinLength() != null ? stringSchema.getMinLength().intValue() : 2;
        int maxLenght = stringSchema.getMaxLength() != null ? stringSchema.getMaxLength().intValue() : 10;
        
        builder.append(" \"");
        // visit format if defined
        if (stringSchema.getFormatValidator().formatName() != "unnamed-format") {
        	visitFormat(stringSchema.getFormatValidator());
        } else {
        	builder.append(options.isGenerateRandomValues() ? generateRandomString(new Random().nextInt(maxLenght) + minLenght) : lastProperty);
        }
        builder.append("\"");
        System.out.println(stringSchema.getPattern());
        
        // visit pattern if defined
        if (stringSchema.getPattern() != null) {
        	visitPattern(stringSchema.getPattern(), minLenght, maxLenght);
        }
    }
    /**
     * Visit the schema referred in the main schema definition.
     * @param referenceSchema
     */
    void visitReferenceSchema(ReferenceSchema referenceSchema) {
        visit(referenceSchema.getReferredSchema());
    }
    /**
     * Appends the property name and visits schema if not empty.
     * @param propertyName
     * @param schema
     */
    void visitPropertySchema(String propertyName, Schema schema) {
    	lastProperty = propertyName;
    	builder.append("\"").append(propertyName).append("\"").append(":");
    	if (schema instanceof EmptySchema) {
    		builder.append("\"\"");
    	} else {
    		visit(schema);
    	}
    	builder.append(",");
    }
    /**
     * Appends a random element of the enumeration if the option is random, or the first element otherwise. 
     * @param enumSchema schema to visit
     */
    void visitEnumSchema(EnumSchema enumSchema) {
    	int randomIndex = new Random().nextInt(enumSchema.getPossibleValues().size());
    	int index = 0;
    	for(Object possibleValue : enumSchema.getPossibleValues()) {
	    	if (index++ == randomIndex || !options.isGenerateRandomValues()) {
	    		builder.append(" \"").append(possibleValue).append("\"");
	    		break;
	    	}
    	}
    }
    void visitCombinedSchema(CombinedSchema combinedSchema) {
    	ValidationCriterion criterion = combinedSchema.getCriterion();
		System.out.println("criterion: " +criterion);
		Collection<Schema> subschemas = combinedSchema.getSubschemas();
		System.out.println(subschemas.size());
		
		if (criterion == CombinedSchema.ALL_CRITERION) {
			for (Schema subschema : subschemas) {
				System.out.print(subschema.getClass() + " ");
				if (subschema instanceof EnumSchema || subschema instanceof ReferenceSchema) {
					visit(subschema);
				}
			}
		} else {
			visit(subschemas.iterator().next());
		}
    }
    void visitSchemaDependency(String propKey, Schema schema) {
    }

    void visitPatternPropertySchema(Regexp propertyNamePattern, Schema schema) {
    }

    void visitSchemaOfAdditionalProperties(Schema schemaOfAdditionalProperties) {
    }

    void visitAdditionalProperties(boolean additionalProperties) {
    }

    void visitPropertyDependencies(String ifPresent, Set<String> allMustBePresent) {
    }

    void visitMaxProperties(Integer maxProperties) {
    }

    void visitMinProperties(Integer minProperties) {
    }

    void visitPropertyNameSchema(Schema propertyNameSchema) {
    }

    void visitRequiredPropertyName(String requiredPropName) {
    }

    /**
     * Appends a string that fits one of the formats described by the validator.
     * @param formatValidator used to get the formatName
     */
    void visitFormat(FormatValidator formatValidator) {
    	if (formatValidator.formatName() == "email") {
    		builder.append (options.isGenerateRandomValues() ? generateRandomString(10).toLowerCase() : "example" + "@domain.com");
    	} else if (formatValidator.formatName() == "date-time") {
    		LocalDateTime dateTime;
    		if (options.isGenerateRandomValues()) {
    			Random r = new Random();
        		dateTime = LocalDateTime.of(r.nextInt(2020)+100, r.nextInt(12)+1, r.nextInt(30)+1, r.nextInt(23), r.nextInt(59));
    		} else {
    			dateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
    		}
    		builder.append(dateTime);
    	} else if (formatValidator.formatName() == "hostname") {
    		builder.append("www");
    	} else if (formatValidator.formatName() == "uri") {
    		builder.append("http://example.com/resource?foo=bar#fragment");
    	} else if (formatValidator.formatName() == "ipv4") {
    		Random r = new Random();
    		builder.append(r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256));
    	} else if (formatValidator.formatName() == "ipv6") {
    		Random r = new Random();
    		builder.append(r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256));
    	}
    }

    void visitPattern(Pattern pattern, Number minLength, Number maxLenth) {
        System.out.println(pattern.pattern());
    	Generex generex = new Generex(pattern.toString());
    	String randomStringToMatch = "";
    	while (randomStringToMatch.length() == 0) {
    		randomStringToMatch = generex.random();
    	}
    	if (randomStringToMatch.startsWith("^")) {
    		randomStringToMatch = randomStringToMatch.substring(1);
    	}
    	if (randomStringToMatch.endsWith("$")) {
    		randomStringToMatch = randomStringToMatch.substring(0, randomStringToMatch.length()-1);
    	}
    	//builder.append(randomStringToMatch);
    	System.out.println(randomStringToMatch + " FROM PATTERN " + pattern.toString() );// a random value from the previous String list
    }

    void visitConditionalSchema(ConditionalSchema conditionalSchema) {
        conditionalSchema.getIfSchema().ifPresent(this::visitIfSchema);
        conditionalSchema.getThenSchema().ifPresent(this::visitThenSchema);
        conditionalSchema.getElseSchema().ifPresent(this::visitElseSchema);
    }

    void visitIfSchema(Schema ifSchema) {
    }

    void visitThenSchema(Schema thenSchema) {
    }

    void visitElseSchema(Schema elseSchema) {
    }
    
    void visitMinItems(Integer minItems) {
    }

    void visitMaxItems(Integer maxItems) {
    }

    void visitUniqueItems(boolean uniqueItems) {
    }

    void visitAllItemSchema(Schema allItemSchema) {
    }

    void visitAdditionalItems(boolean additionalItems) {
    }

    void visitItemSchema(int index, Schema itemSchema) {
    }

    void visitSchemaOfAdditionalItems(Schema schemaOfAdditionalItems) {
    }

    void visitContainedItemSchema(Schema containedItemSchema) {
    	visit(containedItemSchema);
    }
    
    void visitBooleanSchema(BooleanSchema schema) {
    	builder.append(options.isGenerateRandomValues() ? new Random().nextBoolean() : false);
    }

    void visitNullSchema(NullSchema nullSchema) {
    }

    void visitEmptySchema() {
    }

    void visitConstSchema(ConstSchema constSchema) {
    }

    void visitFalseSchema(FalseSchema falseSchema) {
    }

    void visitNotSchema(NotSchema notSchema) {
    }

}
