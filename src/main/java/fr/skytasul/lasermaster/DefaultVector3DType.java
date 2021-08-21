package fr.skytasul.lasermaster;

import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;

import com.karuslabs.commons.command.types.DynamicExampleType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.arguments.coordinates.ArgumentVec3;

public class DefaultVector3DType extends DynamicExampleType<DefaultVector> {
	
	public static final DefaultVector3DType DEFAULT_3D_VECTOR = new DefaultVector3DType();
	
	static final ArgumentVec3 VECTOR_3D = new ArgumentVec3(false);
    static final String[] EMPTY = {};
	
	private DefaultVector3DType() {
		super(List.of("0 0 0", "d d d"));
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(S source, CommandContext<S> context, SuggestionsBuilder builder) {
		var remaining = builder.getRemaining();
		var parts = remaining.isBlank() ? EMPTY : remaining.split(" ");
		
		suggest(builder, parts);
		
		return builder.buildFuture();
	}
    
	public void suggest(SuggestionsBuilder builder, String[] parts) {
        switch (parts.length) {
            case 0 -> builder.suggest("0")
                             .suggest("0 0")
                             .suggest("0 0 0")
                             .suggest("d d d");
            case 1 -> builder.suggest(parts[0] + " 0")
                             .suggest(parts[0] + " 0 0");
			case 2 -> builder.suggest(parts[0] + " " + parts[1] + " 0");
        }
    }
    
    @Override
	public ArgumentType<?> mapped() {
        return VECTOR_3D;
    }
	
	@Override
	public DefaultVector parse(StringReader reader) throws CommandSyntaxException {
		var vector = new DefaultVector();
		
		reader.skipWhitespace();
		vector.setX(readDefault(reader));
		
		reader.skipWhitespace();
		vector.setY(readDefault(reader));
		
		reader.skipWhitespace();
		vector.setZ(readDefault(reader));
		
		return vector;
	}
	
	private OptionalInt readDefault(StringReader reader) throws CommandSyntaxException {
		if (reader.peek() == 'd') {
			reader.skip();
			return OptionalInt.empty();
		}
		return OptionalInt.of(reader.readInt());
	}
	
}
