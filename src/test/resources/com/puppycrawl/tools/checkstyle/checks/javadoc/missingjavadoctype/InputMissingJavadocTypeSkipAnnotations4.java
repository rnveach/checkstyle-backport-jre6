/*
MissingJavadocType
scope = private
excludeScope = (default)null
skipAnnotations = Override
tokens = (default)INTERFACE_DEF, CLASS_DEF, ENUM_DEF, ANNOTATION_DEF, RECORD_DEF


*/

package com.puppycrawl.tools.checkstyle.checks.javadoc.missingjavadoctype;

@ThisIsOk4
class InputMissingJavadocTypeSkipAnnotations4 { // ok
}

@com.puppycrawl.tools.checkstyle.checks.javadoc.missingjavadoctype.ThisIsOk4
class InputJavadocTypeSkipAnnotationsFQN4 { // ok
}

@Generated4(value = "some code generator")
class InputJavadocTypeAllowedAnnotationByDefault4 { // ok
}

/**
 * Annotation for unit tests.
 */
@interface ThisIsOk4 {} // ok
/**
 * Annotation for unit tests.
 */
@interface Generated4 { // ok
    String[] value();
}
