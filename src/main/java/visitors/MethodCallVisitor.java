package visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import utils.StringUtils;

/**
 * @author coldilock
 *
 * Extract method calls in java source code.
 */
public class MethodCallVisitor extends VoidVisitorAdapter {

    /**
     * Method declaration in a java file. That is, the calling method of the method invocation
     * @param n
     * @param arg
     */
    @Override
    public void visit(MethodDeclaration n, Object arg) {
        // get the name of the calling method
        String methodName = n.getNameAsString();
        // get the return type of the calling method
        String methodReturnType = n.getType().toString();
        String methodSignature;
        try{
            // get the qualified signature of the calling method using JavaSymbolSolver
            methodSignature = n.resolve().getQualifiedSignature();
        } catch (Exception e){
            methodSignature = "UnsolvedType.In.MethodDeclaration.method()";
        }

        if(StringUtils.isValidMethodName(methodSignature)){
            // System.out.printf("[callingMethodName]: %s [returnType]: %s [methodSignature]: %s%n", methodName, methodReturnType, methodSignature);
            System.out.printf("[Calling Method]: %s\t[returnType]: %s%n", methodSignature, methodReturnType);
        }

        super.visit(n, arg);
    }

    /**
     * Method call in a method. That is, the called method of the method invocation
     * @param n
     * @param arg
     */
    @Override
    public void visit(MethodCallExpr n, Object arg) {
        /*
         * Calling the visit method first of the super class can handle the following situations:
         * (1) If the parameter of the method call is also a method call, the parameter will be processed first.
         * (2) If there are continuous method calls, process these called methods from left to right.
         */
        super.visit(n, arg);

        // get the name of theOr if called method
        String methodName = n.getNameAsString();
        String methodReturnType = "";
        String methodSignature;
        try{
            // get the qualified signature and return type of the called method using JavaSymbolSolver
            methodSignature = n.resolve().getQualifiedSignature();
            methodReturnType = n.resolve().getReturnType().describe();
        } catch (Exception e){
            methodSignature = "UnsolvedType.In.MethodCallExpr.method()";
        }

        if(StringUtils.isValidMethodName(methodSignature)){
            // System.out.printf("\t[calledMethodName]: %s [returnType]: %s [methodSignature]: %s%n", methodName, methodReturnType, methodSignature);
            System.out.printf("\t[Called Method]: %s\t[returnType]: %s%n", methodSignature, methodReturnType);
        }
    }

}
