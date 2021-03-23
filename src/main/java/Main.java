import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import visitors.MethodCallVisitor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author coldilock
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String javaFilePath = "src/main/resources/testcase/test1.java";
        getMethodCalls(javaFilePath);
    }

    /**
     * get all method calls of a java source file
     * @param javaFilePath java source file path
     * @throws IOException
     */
    public static void getMethodCalls(String javaFilePath) throws IOException {
        JavaSymbolSolver symbolSolver = configureJavaSymbolSolver(true, true, true);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        // cu is the AST root
        CompilationUnit cu = StaticJavaParser.parse(new File(javaFilePath));
        // find all methods in the java file, and get all method invocations for each method
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            MethodCallVisitor visitor = new MethodCallVisitor();
            method.accept(visitor, null);
        });
    }

    /**
     * Configure the JavaSymbolSolver
     * @param isResolvingJdkAPI resolve types from JDK API or not
     * @param isResolvingThirdPartyAPI resolve types from Third Party APIs or not
     * @param isResolvingUserDefinedAPI resolve types from User-defined Classes or not
     * @throws IOException
     */
    public static JavaSymbolSolver configureJavaSymbolSolver(boolean isResolvingJdkAPI, boolean isResolvingThirdPartyAPI, boolean isResolvingUserDefinedAPI) throws IOException {
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();

        /*
         * Resolve qualified types/names from JDK API
         * e.g. String str -> the type of "str" is 'java.lang.String'
         */
        if(isResolvingJdkAPI){
            typeSolver.add(new ReflectionTypeSolver());
        }
        /*
         * Resolve qualified types/names from Third Party APIs
         * e.g. CollectionUtils.emptyIfNull(xxx) -> org.apache.commons.collections4.CollectionUtils.emptyIfNull(java.util.Collection<T>)
         */
        if(isResolvingThirdPartyAPI){
            List<String> jarFileList = Arrays.asList(
                    "src/main/resources/jarfiles/commons-collections4-4.4.jar",
                    "src/main/resources/jarfiles/javaparser-core-3.16.1.jar"
            );
            // set the jar file paths to JarTypeSolver
            for(String jarFilePath : jarFileList){
                typeSolver.add(JarTypeSolver.getJarTypeSolver(jarFilePath));
            }
        }
        /*
         * Resolve qualified types/names from User-defined Classes and Methods.
         */
        if(isResolvingUserDefinedAPI){
            // Specify the source root path where the target file is located
            String projectSrcPath = "src/main/java";
            typeSolver.add(new JavaParserTypeSolver(new File(projectSrcPath)));
        }

        return new JavaSymbolSolver(typeSolver);
    }
}
