package net.teamfruit.emojicord.asm;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import net.teamfruit.emojicord.Log;
import net.teamfruit.emojicord.asm.lib.ClassName;
import net.teamfruit.emojicord.asm.lib.INodeTransformer;
import net.teamfruit.emojicord.asm.lib.INodeTreeTransformer;
import net.teamfruit.emojicord.asm.lib.VisitorHelper;
import net.teamfruit.emojicord.compat.CompatTransformer;

public class EmojicordTransformer extends CompatTransformer {
	@Override
	public ClassNode read(@Nonnull final byte[] bytes) {
		return VisitorHelper.read(bytes, ClassReader.SKIP_FRAMES);
	}

	@Override
	public byte[] write(@Nonnull final ClassNode node) {
		return VisitorHelper.write(node, ClassWriter.COMPUTE_FRAMES);
	}

	private final INodeTreeTransformer transformers[] = {
			new SendChatMessageTransform(),
			new GuiTextFieldTransform(),
			new FontRendererTransform(),
			new GuiScreenInputEventTransform(),
	};

	private final Set<String> transformerNames = Stream.of(this.transformers).map(INodeTransformer::getClassName).map(ClassName::getName).collect(Collectors.toSet());

	@Override
	public ClassNode transform(final ClassNode input, final CompatTransformerVotingContext context) {
		try {
			for (final INodeTreeTransformer transformer : this.transformers)
				if (transformer.getMatcher().test(input))
					return VisitorHelper.transform(input, transformer, Log.log);
		} catch (final Exception e) {
			throw new RuntimeException("Could not transform: ", e);
		}

		return input;
	}

	DeferredTransform[] deferredTransforms = {
			new DeferredTransform(EmojicordTransformer.class.getName(), "com.tsoft_web.IntelliInput.asm.IntelliInputTransformer"),
	};

	@Override
	public DeferredTransform[] deferredTransforms() {
		return this.deferredTransforms;
	}

	@Override
	public Set<String> targetNames() {
		return this.transformerNames;
	}
}