/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.support.annotation;

import java.lang.annotation.Annotation;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.util.Assert;

/**
 * Simple pointcut that looks for a specific Java 5 annotation
 * being present on a class or method.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see AnnotationClassFilter
 */
public class AnnotationMatchingPointcut implements Pointcut {

	private final ClassFilter classFilter;

	private final MethodMatcher methodMatcher;


	/**
	 * Create a new AnnotationMatchingPointcut for the given annotation type.
	 * @param classAnnotationType the annotation type to look for at the class level
	 */
	public AnnotationMatchingPointcut(Class<? extends Annotation> classAnnotationType) {
		this(classAnnotationType, null);
	}

	/**
	 * Create a new AnnotationMatchingPointcut for the given annotation type.
	 * @param classAnnotationType the annotation type to look for at the class level
	 * (can be <code>null</code>)
	 * @param methodAnnotationType the annotation type to look for at the method level
	 * (can be <code>null</code>)
	 */
	public AnnotationMatchingPointcut(
			Class<? extends Annotation> classAnnotationType, Class<? extends Annotation> methodAnnotationType) {

		Assert.isTrue((classAnnotationType != null || methodAnnotationType != null),
				"Either Class annotation type or Method annotation type needs to be specified (or both)");

		if (classAnnotationType != null) {
			this.classFilter = new AnnotationClassFilter(classAnnotationType);
		}
		else {
			this.classFilter = ClassFilter.TRUE;
		}

		if (methodAnnotationType != null) {
			this.methodMatcher = new AnnotationMethodMatcher(methodAnnotationType);
		}
		else {
			this.methodMatcher = MethodMatcher.TRUE;
		}
	}


	public ClassFilter getClassFilter() {
		return this.classFilter;
	}

	public MethodMatcher getMethodMatcher() {
		return this.methodMatcher;
	}

}
