/**
 * Name: Cai Yuejun Leon
 * 
 * This allows building of annotation on top of java objects to differentiate "required and optional properties".
 */
package ExtractTransform;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface AllKeysRequired {
}