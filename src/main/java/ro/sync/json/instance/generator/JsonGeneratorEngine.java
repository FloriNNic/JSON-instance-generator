package ro.sync.json.instance.generator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import org.everit.json.schema.*;
import org.everit.json.schema.CombinedSchema.ValidationCriterion;
import org.everit.json.schema.regexp.Regexp;

import com.mifmif.common.regex.Generex;

public class JsonGeneratorEngine {
	
	StringBuilder builder = new StringBuilder();
	private JsonGeneratorOptions options;
	
	private String lastProperty;
	
	public JsonGeneratorEngine(JsonGeneratorOptions options) {
		this.options = options;
	}
	//TODO refactor
	public int getRandomNumberBetween (Number min, Number max) {
		int randomNumber = (min != null) ? (int) min : 0;
		Random rand = new Random();

		if (min != null && max != null) {
			randomNumber = rand.nextInt((int) max - (int) min + 1) + (int)min;
		}else if (min == null && max !=null) {
			randomNumber = rand.nextInt((int) max);
		}else if (min != null && max == null){
			randomNumber = rand.nextInt(100 + (int)min) + (int)min;
		}else {
			randomNumber = rand.nextInt(10);
		}
		return randomNumber;
	}
	
	public String generateRandomString(int length) {
    	final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return stringBuilder.toString();
    }
	
    void visit(Schema schema) {
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
     * Appends an integer value between minimum and maximum. If minimum and maximum are not defined, they are
     * assigned values 0 and 100, respectively. 
     * @param numberSchema schema to visit
     */
    void visitNumberSchema(NumberSchema numberSchema) {
        visitExclusiveMinimum(numberSchema.isExclusiveMinimum());
        visitMinimum(numberSchema.getMinimum());
        visitExclusiveMinimumLimit(numberSchema.getExclusiveMinimumLimit());
        
        visitExclusiveMaximum(numberSchema.isExclusiveMaximum());
        visitMaximum(numberSchema.getMaximum());
        visitExclusiveMaximumLimit(numberSchema.getExclusiveMaximumLimit());
        visitMultipleOf(numberSchema.getMultipleOf());
        
        int minimum = (numberSchema.getMinimum() != null) ? numberSchema.getMinimum().intValue() : 0;
        int maximum = (numberSchema.getMaximum() != null) ? numberSchema.getMaximum().intValue() : 100;
        
        int exclusiveMinimum = (numberSchema.isExclusiveMinimum() ? 1:0 );
        int exclusiveMaximum = (numberSchema.isExclusiveMaximum() ? 1:0 );
        
        builder.append(" ").append(options.isGenerateRandomValues() ? getRandomNumberBetween(minimum + exclusiveMinimum, maximum - exclusiveMaximum) : minimum);
    }
    
    /**
     * Appends a random number of items if the option is random, or 2 items by default. The items use the lastPropertyName 
     * as they have the same name + an increasing index.
     * @param arraySchema schema to visit
     */
    void visitArraySchema(ArraySchema arraySchema) {
    	// get minimum and maximum number of items the schema can contain
        visitMinItems(arraySchema.getMinItems());
        Number minimum = arraySchema.getMinItems();
        visitMaxItems(arraySchema.getMaxItems());
        Number maximum = arraySchema.getMaxItems();
        
        String lastPropertyName = lastProperty;
        int numberOfItems = options.isGenerateRandomValues() ? getRandomNumberBetween (minimum, maximum) : 2;
        
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
        visitContainedItemSchema(arraySchema.getContainedItemSchema());
    }
    /**
     * Visit the schema that is referred in the main schema definition.
     * @param referenceSchema
     */
    void visitReferenceSchema(ReferenceSchema referenceSchema) {
        visit(referenceSchema.getReferredSchema());
    }

    /**
     * 
     * @param objectSchema schema to visit
     */
    private void visitObjectSchema(ObjectSchema objectSchema) {
    	builder.append("{");
        visitPropertyNameSchema(objectSchema.getPropertyNameSchema());
        visitMinProperties(objectSchema.getMinProperties());
        visitMaxProperties(objectSchema.getMaxProperties());
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
     * 
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
     * Appends a random string element if the option is random, or the name of the property otherwise.
     * @param stringSchema schema to visit
     */
    void visitStringSchema(StringSchema stringSchema) {
    	// get minimum and maximum length the schema can have
    	visitMinLength(stringSchema.getMinLength());
        Number minLenght = stringSchema.getMinLength();
        visitMaxLength(stringSchema.getMaxLength());
        Number maxLenght = stringSchema.getMaxLength();
        
        builder.append(" \"");
        builder.append(options.isGenerateRandomValues() ? generateRandomString(getRandomNumberBetween(minLenght, maxLenght)) : lastProperty);
        builder.append("\"");
        
        System.out.println(stringSchema.getPattern());
        if (stringSchema.getPattern() != null) {
        	visitPattern(stringSchema.getPattern(), minLenght, maxLenght);
        }
        //visitFormat(stringSchema.getFormatValidator());
    }
    
    /**
     * Appends a random element of the enumeration if the option is random, or the first element otherwise. 
     * @param enumSchema schema to visit
     */
    void visitEnumSchema(EnumSchema enumSchema) {
    	int randomIndex = getRandomNumberBetween(1, enumSchema.getPossibleValues().size());
    	int index = 1;
    	for(Object possibleValue : enumSchema.getPossibleValues()) {
	    	if (index++ == randomIndex || !options.isGenerateRandomValues()) {
	    		builder.append(" \"").append(possibleValue).append("\"");
	    		break;
	    	}
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

    void visitFormat(FormatValidator formatValidator) {
    	System.out.println(formatValidator.formatName());
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

    void visitMaxLength(Integer maxLength) {
    }

    void visitMinLength(Integer minLength) {
    }

    void visitCombinedSchema(CombinedSchema combinedSchema) {
    	ValidationCriterion criterion = combinedSchema.getCriterion();
		System.out.println("criterion: " +criterion);
		Collection<Schema> subschemas = combinedSchema.getSubschemas();
		System.out.println(subschemas.size());
		
		for (Schema subschema : subschemas) {
			System.out.println("Subschema: " + subschema);
			System.out.println(subschema.getClass());
			visit(subschema);
			System.out.println("Builder so far: " + builder.toString());
			
			if(criterion == CombinedSchema.ANY_CRITERION || criterion == CombinedSchema.ONE_CRITERION || subschema instanceof EnumSchema) {
				break;
			}
		}
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
    
    void visitMinimum(Number minimum) {
    }

    void visitExclusiveMinimum(boolean exclusiveMinimum) {
    }

    void visitExclusiveMinimumLimit(Number exclusiveMinimumLimit) {
    }

    void visitMaximum(Number maximum) {
    }

    void visitExclusiveMaximum(boolean exclusiveMaximum) {
    }

    void visitExclusiveMaximumLimit(Number exclusiveMaximumLimit) {
    }

    void visitMultipleOf(Number multipleOf) {
    }
    
    /*
     * 	static void visitObjectSchema(ObjectSchema objectSchema) {
        for (String requiredPropName : objectSchema.getRequiredProperties()) {
            System.out.println("requiredPropName " + requiredPropName);
           
        }
        System.out.println(objectSchema.getPropertySchemas());
        System.out.println(objectSchema.getPropertyNameSchema());
        System.out.println(objectSchema.getMinProperties());
        System.out.println(objectSchema.getMaxProperties());
        for (Map.Entry<String, Set<String>> entry : objectSchema.getPropertyDependencies().entrySet()) {
        	System.out.println(entry.getKey() + entry.getValue());
        }
        System.out.println(objectSchema.permitsAdditionalProperties());
        System.out.println(objectSchema.getSchemaOfAdditionalProperties());
        
        // TODO 
        for (Entry<Pattern, Schema> entry : objectSchema.getPatternProperties().entrySet()) {
            //visitPatternPropertySchema(entry.getKey(), entry.getValue());
        	System.out.println("pattern: " + entry.getKey() + " schema: " + entry.getValue());
        }
        
 //       for (Map.Entry<String, Schema> schemaDep : objectSchema.getSchemaDependencies().entrySet()) {
 //       }
        
    }
     */

}
