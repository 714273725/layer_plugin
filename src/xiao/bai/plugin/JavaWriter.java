package xiao.bai.plugin;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Modifier;

public class JavaWriter implements Closeable {
    private static final Pattern TYPE_TRAILER = Pattern.compile("(.*?)(\\.\\.\\.|(?:\\[\\])+)$");
    private static final Pattern TYPE_PATTERN = Pattern.compile("(?:[\\w$]+\\.)*([\\w\\.*$]+)");
    private static final int MAX_SINGLE_LINE_ATTRIBUTES = 3;
    private static final String INDENT = "  ";
    private final Map<String, String> importedTypes = new LinkedHashMap();
    private String packagePrefix;
    private final Deque<Scope> scopes = new ArrayDeque();
    private final Deque<String> types = new ArrayDeque();
    private final Writer out;
    private boolean isCompressingTypes = true;
    private String indent = "  ";
    private static final EnumSet<Scope> METHOD_SCOPES;

    public JavaWriter(Writer out) {
        this.out = out;
    }

    public void setCompressingTypes(boolean isCompressingTypes) {
        this.isCompressingTypes = isCompressingTypes;
    }

    public boolean isCompressingTypes() {
        return this.isCompressingTypes;
    }

    public void setIndent(String indent) {
        this.indent = indent;
    }

    public String getIndent() {
        return this.indent;
    }

    public JavaWriter emitPackage(String packageName) throws IOException {
        if (this.packagePrefix != null) {
            throw new IllegalStateException();
        } else {
            if (packageName.isEmpty()) {
                this.packagePrefix = "";
            } else {
                this.out.write("package ");
                this.out.write(packageName);
                this.out.write(";\n\n");
                this.packagePrefix = packageName + ".";
            }

            return this;
        }
    }

    public JavaWriter emitImports(String... types) throws IOException {
        return this.emitImports((Collection) Arrays.asList(types));
    }

    public JavaWriter emitImports(Class... types) throws IOException {
        List<String> classNames = new ArrayList(types.length);
        Class[] var3 = types;
        int var4 = types.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Class<?> classToImport = var3[var5];
            classNames.add(classToImport.getCanonicalName());
        }

        return this.emitImports((Collection) classNames);
    }

    public JavaWriter emitImports(Collection<String> types) throws IOException {
        Iterator var2 = (new TreeSet(types)).iterator();

        while (var2.hasNext()) {
            String type = (String) var2.next();
            Matcher matcher = TYPE_PATTERN.matcher(type);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(type);
            }

            //忽略重复的import
            if (this.importedTypes.put(type, matcher.group(1)) != null) {
                return this;
            }

            this.out.write("import ");
            this.out.write(type);
            this.out.write(";\n");
        }

        return this;
    }

    public JavaWriter emitStaticImports(String... types) throws IOException {
        return this.emitStaticImports((Collection) Arrays.asList(types));
    }

    public JavaWriter emitReturn(String returnName) throws IOException {
        this.out.write("\n");
        this.out.write("return " + returnName + ";" + "\n");
        return this;
    }

    public JavaWriter emitSuperConstructor(String... params) throws IOException {
        this.out.write("super(");
        for (int i = 0; i < params.length; i++) {
            if (i == params.length - 1) {
                this.out.write(params[i]);
            } else {
                this.out.write(params[i] + ",");
            }
        }
        this.out.write(");\n");
        return this;
    }

    public JavaWriter emitSuper(String methodName, String... params) throws IOException {
        this.out.write("super." + methodName + "(");
        if (params == null || params.length == 0) {
            this.out.write(");\n");
        } else {
            for (int i = 0; i < params.length; i++) {
                if (i == params.length - 1) {
                    this.out.write(params[i]);
                } else {
                    this.out.write(params[i] + ",");
                }
            }
            this.out.write(");\n");
        }
        return this;
    }


    public JavaWriter emitStaticImports(Collection<String> types) throws IOException {
        Iterator var2 = (new TreeSet(types)).iterator();

        while (var2.hasNext()) {
            String type = (String) var2.next();
            Matcher matcher = TYPE_PATTERN.matcher(type);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(type);
            }

            if (this.importedTypes.put(type, matcher.group(1)) != null) {
                throw new IllegalArgumentException(type);
            }

            this.out.write("import static ");
            this.out.write(type);
            this.out.write(";\n");
        }

        return this;
    }

    private JavaWriter emitCompressedType(String type) throws IOException {
        if (this.isCompressingTypes) {
            this.out.write(this.compressType(type));
        } else {
            this.out.write(type);
        }

        return this;
    }

    public String compressType(String type) {
        Matcher trailer = TYPE_TRAILER.matcher(type);
        if (trailer.matches()) {
            type = trailer.group(1);
        }

        StringBuilder sb = new StringBuilder();
        if (this.packagePrefix == null) {
            throw new IllegalStateException();
        } else {
            Matcher m = TYPE_PATTERN.matcher(type);
            int pos = 0;

            while (true) {
                boolean found = m.find(pos);
                int typeStart = found ? m.start() : type.length();
                sb.append(type, pos, typeStart);
                if (!found) {
                    if (trailer.matches()) {
                        sb.append(trailer.group(2));
                    }

                    return sb.toString();
                }

                String name = m.group(0);
                String imported = (String) this.importedTypes.get(name);
                if (imported != null) {
                    sb.append(imported);
                } else if (isClassInPackage(name, this.packagePrefix)) {
                    String compressed = name.substring(this.packagePrefix.length());
                    if (this.isAmbiguous(compressed)) {
                        sb.append(name);
                    } else {
                        sb.append(compressed);
                    }
                } else if (isClassInPackage(name, "java.lang.")) {
                    sb.append(name.substring("java.lang.".length()));
                } else {
                    sb.append(name);
                }

                pos = m.end();
            }
        }
    }

    private static boolean isClassInPackage(String name, String packagePrefix) {
        if (name.startsWith(packagePrefix)) {
            if (name.indexOf(46, packagePrefix.length()) == -1) {
                return true;
            }

            if (Character.isUpperCase(name.charAt(packagePrefix.length()))) {
                return true;
            }
        }

        return false;
    }

    private boolean isAmbiguous(String compressed) {
        return this.importedTypes.values().contains(compressed);
    }

    public JavaWriter beginInitializer(boolean isStatic) throws IOException {
        this.indent();
        if (isStatic) {
            this.out.write("static");
            this.out.write(" {\n");
        } else {
            this.out.write("{\n");
        }

        this.scopes.push(Scope.INITIALIZER);
        return this;
    }

    public JavaWriter endInitializer() throws IOException {
        this.popScope(new Scope[]{Scope.INITIALIZER});
        this.indent();
        this.out.write("}\n");
        return this;
    }

    public JavaWriter beginType(String type, String kind) throws IOException {
        return this.beginType(type, kind, EnumSet.noneOf(Modifier.class), (String) null, new String[0]);
    }

    public JavaWriter beginType(String type, String kind, Set<Modifier> modifiers) throws IOException {
        return this.beginType(type, kind, modifiers, (String) null, new String[0]);
    }

    public JavaWriter beginType(String type, String kind, Set<Modifier> modifiers, String extendsType, String... implementsTypes) throws IOException {
        this.indent();
        this.emitModifiers(modifiers);
        this.out.write(kind);
        this.out.write(" ");
        this.emitCompressedType(type);
        if (extendsType != null) {
            this.out.write(" extends ");
            this.emitCompressedType(extendsType);
        }

        if (implementsTypes.length > 0) {
            this.out.write("\n");
            this.indent();
            this.out.write("    implements ");

            for (int i = 0; i < implementsTypes.length; ++i) {
                if (i != 0) {
                    this.out.write(", ");
                }

                this.emitCompressedType(implementsTypes[i]);
            }
        }

        this.out.write(" {\n");
        this.scopes.push("interface".equals(kind) ? Scope.INTERFACE_DECLARATION : Scope.TYPE_DECLARATION);
        this.types.push(type);
        return this;
    }

    public JavaWriter endType() throws IOException {
        this.popScope(new Scope[]{Scope.TYPE_DECLARATION, Scope.INTERFACE_DECLARATION});
        this.types.pop();
        this.indent();
        this.out.write("}\n");
        return this;
    }

    public JavaWriter emitField(String type, String name) throws IOException {
        return this.emitField(type, name, EnumSet.noneOf(Modifier.class), (String) null);
    }

    public JavaWriter emitField(String type, String name, Set<Modifier> modifiers) throws IOException {
        return this.emitField(type, name, modifiers, (String) null);
    }

    public JavaWriter emitField(String type, String name, Set<Modifier> modifiers, String initialValue) throws IOException {
        this.indent();
        this.emitModifiers(modifiers);
        this.emitCompressedType(type);
        this.out.write(" ");
        this.out.write(name);
        if (initialValue != null) {
            this.out.write(" =");
            if (!initialValue.startsWith("\n")) {
                this.out.write(" ");
            }

            String[] lines = initialValue.split("\n", -1);
            this.out.write(lines[0]);

            for (int i = 1; i < lines.length; ++i) {
                this.out.write("\n");
                this.hangingIndent();
                this.out.write(lines[i]);
            }
        }

        this.out.write(";\n");
        return this;
    }

    public JavaWriter beginMethod(String returnType, String name, Set<Modifier> modifiers, String... parameters) throws IOException {
        return this.beginMethod(returnType, name, modifiers, Arrays.asList(parameters), (List) null);
    }

    public JavaWriter beginMethod(String returnType, String name, Set<Modifier> modifiers, List<String> parameters, List<String> throwsTypes) throws IOException {
        this.indent();
        this.emitModifiers(modifiers);
        if (returnType != null) {
            this.emitCompressedType(returnType);
            this.out.write(" ");
            this.out.write(name);
        } else {
            this.emitCompressedType(name);
        }

        this.out.write("(");
        int i;
        if (parameters != null) {
            i = 0;

            while (i < parameters.size()) {
                if (i != 0) {
                    this.out.write(", ");
                }

                this.emitCompressedType((String) parameters.get(i++));
                this.out.write(" ");
                this.emitCompressedType((String) parameters.get(i++));
            }
        }

        this.out.write(")");
        if (throwsTypes != null && throwsTypes.size() > 0) {
            this.out.write("\n");
            this.indent();
            this.out.write("    throws ");

            for (i = 0; i < throwsTypes.size(); ++i) {
                if (i != 0) {
                    this.out.write(", ");
                }

                this.emitCompressedType((String) throwsTypes.get(i));
            }
        }

        if (!modifiers.contains(Modifier.ABSTRACT) && !Scope.INTERFACE_DECLARATION.equals(this.scopes.peek())) {
            this.out.write(" {\n");
            this.scopes.push(returnType == null ? Scope.CONSTRUCTOR : Scope.NON_ABSTRACT_METHOD);
        } else {
            this.out.write(";\n");
            this.scopes.push(Scope.ABSTRACT_METHOD);
        }

        return this;
    }

    public JavaWriter beginConstructor(Set<Modifier> modifiers, String... parameters) throws IOException {
        this.beginMethod((String) null, rawType((String) this.types.peekFirst()), modifiers, parameters);
        return this;
    }

    public JavaWriter beginConstructor(Set<Modifier> modifiers, List<String> parameters, List<String> throwsTypes) throws IOException {
        this.beginMethod((String) null, rawType((String) this.types.peekFirst()), modifiers, parameters, throwsTypes);
        return this;
    }

    public JavaWriter emitJavadoc(String javadoc, Object... params) throws IOException {
        String formatted = String.format(javadoc, params);
        this.indent();
        this.out.write("/**\n");
        String[] var4 = formatted.split("\n");
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            String line = var4[var6];
            this.indent();
            this.out.write(" *");
            if (!line.isEmpty()) {
                this.out.write(" ");
                this.out.write(line);
            }

            this.out.write("\n");
        }

        this.indent();
        this.out.write(" */\n");
        return this;
    }

    public JavaWriter emitSingleLineComment(String comment, Object... args) throws IOException {
        this.indent();
        this.out.write("// ");
        this.out.write(String.format(comment, args));
        this.out.write("\n");
        return this;
    }

    public JavaWriter emitEmptyLine() throws IOException {
        this.out.write("\n");
        return this;
    }

    public JavaWriter emitEnumValue(String name) throws IOException {
        this.indent();
        this.out.write(name);
        this.out.write(",\n");
        return this;
    }

    public JavaWriter emitEnumValue(String name, boolean isLast) throws IOException {
        return isLast ? this.emitLastEnumValue(name) : this.emitEnumValue(name);
    }

    private JavaWriter emitLastEnumValue(String name) throws IOException {
        this.indent();
        this.out.write(name);
        this.out.write(";\n");
        return this;
    }

    public JavaWriter emitEnumValues(Iterable<String> names) throws IOException {
        Iterator iterator = names.iterator();

        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            if (iterator.hasNext()) {
                this.emitEnumValue(name);
            } else {
                this.emitLastEnumValue(name);
            }
        }

        return this;
    }

    public JavaWriter emitAnnotation(String annotation) throws IOException {
        return this.emitAnnotation(annotation, Collections.emptyMap());
    }

    public JavaWriter emitAnnotation(Class<? extends Annotation> annotationType) throws IOException {
        return this.emitAnnotation(type(annotationType, new String[0]), Collections.emptyMap());
    }

    public JavaWriter emitAnnotation(Class<? extends Annotation> annotationType, Object value) throws IOException {
        return this.emitAnnotation(type(annotationType, new String[0]), value);
    }

    public JavaWriter emitAnnotation(String annotation, Object value) throws IOException {
        this.indent();
        this.out.write("@");
        this.emitCompressedType(annotation);
        this.out.write("(");
        this.emitAnnotationValue(value);
        this.out.write(")");
        this.out.write("\n");
        return this;
    }

    public JavaWriter emitAnnotation(Class<? extends Annotation> annotationType, Map<String, ?> attributes) throws IOException {
        return this.emitAnnotation(type(annotationType, new String[0]), attributes);
    }

    public JavaWriter emitAnnotation(String annotation, Map<String, ?> attributes) throws IOException {
        this.indent();
        this.out.write("@");
        this.emitCompressedType(annotation);
        switch (attributes.size()) {
            case 0:
                break;
            case 1:
                Map.Entry<String, ?> onlyEntry = (Map.Entry) attributes.entrySet().iterator().next();
                this.out.write("(");
                if (!"value".equals(onlyEntry.getKey())) {
                    this.out.write((String) onlyEntry.getKey());
                    this.out.write(" = ");
                }

                this.emitAnnotationValue(onlyEntry.getValue());
                this.out.write(")");
                break;
            default:
                boolean split = attributes.size() > 3 || this.containsArray(attributes.values());
                this.out.write("(");
                this.scopes.push(Scope.ANNOTATION_ATTRIBUTE);
                String separator = split ? "\n" : "";
                Iterator var6 = attributes.entrySet().iterator();

                while (var6.hasNext()) {
                    Map.Entry<String, ?> entry = (Map.Entry) var6.next();
                    this.out.write(separator);
                    separator = split ? ",\n" : ", ";
                    if (split) {
                        this.indent();
                    }

                    this.out.write((String) entry.getKey());
                    this.out.write(" = ");
                    Object value = entry.getValue();
                    this.emitAnnotationValue(value);
                }

                this.popScope(new Scope[]{Scope.ANNOTATION_ATTRIBUTE});
                if (split) {
                    this.out.write("\n");
                    this.indent();
                }

                this.out.write(")");
        }

        this.out.write("\n");
        return this;
    }

    private boolean containsArray(Collection<?> values) {
        Iterator var2 = values.iterator();

        Object value;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            value = var2.next();
        } while (!(value instanceof Object[]));

        return true;
    }

    private JavaWriter emitAnnotationValue(Object value) throws IOException {
        if (value instanceof Object[]) {
            this.out.write("{");
            boolean firstValue = true;
            this.scopes.push(Scope.ANNOTATION_ARRAY_VALUE);
            Object[] var3 = (Object[]) ((Object[]) value);
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Object o = var3[var5];
                if (firstValue) {
                    firstValue = false;
                    this.out.write("\n");
                } else {
                    this.out.write(",\n");
                }

                this.indent();
                this.out.write(o.toString());
            }

            this.popScope(new Scope[]{Scope.ANNOTATION_ARRAY_VALUE});
            this.out.write("\n");
            this.indent();
            this.out.write("}");
        } else {
            this.out.write(value.toString());
        }

        return this;
    }

    public JavaWriter emitStatement(String pattern, Object... args) throws IOException {
        this.checkInMethod();
        String[] lines = String.format(pattern, args).split("\n", -1);
        this.indent();
        this.out.write(lines[0]);

        for (int i = 1; i < lines.length; ++i) {
            this.out.write("\n");
            this.hangingIndent();
            this.out.write(lines[i]);
        }

        this.out.write(";\n");
        return this;
    }

    public JavaWriter beginControlFlow(String controlFlow) throws IOException {
        return this.beginControlFlow(controlFlow, new Object[0]);
    }

    public JavaWriter beginControlFlow(String controlFlow, Object... args) throws IOException {
        this.checkInMethod();
        this.indent();
        this.out.write(String.format(controlFlow, args));
        this.out.write(" {\n");
        this.scopes.push(Scope.CONTROL_FLOW);
        return this;
    }

    public JavaWriter nextControlFlow(String controlFlow) throws IOException {
        return this.nextControlFlow(controlFlow, new Object[0]);
    }

    public JavaWriter nextControlFlow(String controlFlow, Object... args) throws IOException {
        this.popScope(new Scope[]{Scope.CONTROL_FLOW});
        this.indent();
        this.scopes.push(Scope.CONTROL_FLOW);
        this.out.write("} ");
        this.out.write(String.format(controlFlow, args));
        this.out.write(" {\n");
        return this;
    }

    public JavaWriter endControlFlow() throws IOException {
        return this.endControlFlow((String) null);
    }

    public JavaWriter endControlFlow(String controlFlow) throws IOException {
        return this.endControlFlow(controlFlow, new Object[0]);
    }

    public JavaWriter endControlFlow(String controlFlow, Object... args) throws IOException {
        this.popScope(new Scope[]{Scope.CONTROL_FLOW});
        this.indent();
        if (controlFlow != null) {
            this.out.write("} ");
            this.out.write(String.format(controlFlow, args));
            this.out.write(";\n");
        } else {
            this.out.write("}\n");
        }

        return this;
    }

    public JavaWriter endMethod() throws IOException {
        Scope popped = (Scope) this.scopes.pop();
        if (popped != Scope.NON_ABSTRACT_METHOD && popped != Scope.CONSTRUCTOR) {
            if (popped != Scope.ABSTRACT_METHOD) {
                throw new IllegalStateException();
            }
        } else {
            this.indent();
            this.out.write("}\n");
        }

        return this;
    }

    public JavaWriter endConstructor() throws IOException {
        this.popScope(new Scope[]{Scope.CONSTRUCTOR});
        this.indent();
        this.out.write("}\n");
        return this;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static String stringLiteral(String data) {
        return StringLiteral.forValue(data).literal();
    }

    public static String type(Class<?> raw, String... parameters) {
        if (parameters.length == 0) {
            return raw.getCanonicalName();
        } else if (raw.getTypeParameters().length != parameters.length) {
            throw new IllegalArgumentException();
        } else {
            StringBuilder result = new StringBuilder();
            result.append(raw.getCanonicalName());
            result.append("<");
            result.append(parameters[0]);

            for (int i = 1; i < parameters.length; ++i) {
                result.append(", ");
                result.append(parameters[i]);
            }

            result.append(">");
            return result.toString();
        }
    }

    public static String rawType(String type) {
        int lessThanIndex = type.indexOf(60);
        return lessThanIndex != -1 ? type.substring(0, lessThanIndex) : type;
    }

    public void close() throws IOException {
        this.out.close();
    }

    private void emitModifiers(Set<Modifier> modifiers) throws IOException {
        if (!((Set) modifiers).isEmpty()) {
            if (!(modifiers instanceof EnumSet)) {
                modifiers = EnumSet.copyOf((Collection) modifiers);
            }

            Iterator var2 = ((Set) modifiers).iterator();

            while (var2.hasNext()) {
                Modifier modifier = (Modifier) var2.next();
                this.out.append(modifier.toString()).append(' ');
            }

        }
    }

    private void indent() throws IOException {
        int i = 0;

        for (int count = this.scopes.size(); i < count; ++i) {
            this.out.write(this.indent);
        }

    }

    private void hangingIndent() throws IOException {
        int i = 0;

        for (int count = this.scopes.size() + 2; i < count; ++i) {
            this.out.write(this.indent);
        }

    }

    private void checkInMethod() {
        if (!METHOD_SCOPES.contains(this.scopes.peekFirst())) {
            throw new IllegalArgumentException();
        }
    }

    private void popScope(Scope... expected) {
        if (!EnumSet.copyOf(Arrays.asList(expected)).contains(this.scopes.pop())) {
            throw new IllegalStateException();
        }
    }

    static {
        METHOD_SCOPES = EnumSet.of(Scope.NON_ABSTRACT_METHOD, Scope.CONSTRUCTOR, Scope.CONTROL_FLOW, Scope.INITIALIZER);
    }

    private static enum Scope {
        TYPE_DECLARATION,
        INTERFACE_DECLARATION,
        ABSTRACT_METHOD,
        NON_ABSTRACT_METHOD,
        CONSTRUCTOR,
        CONTROL_FLOW,
        ANNOTATION_ATTRIBUTE,
        ANNOTATION_ARRAY_VALUE,
        INITIALIZER;

        private Scope() {
        }
    }
}