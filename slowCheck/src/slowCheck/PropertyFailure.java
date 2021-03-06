package slowCheck;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author peter
 */
public interface PropertyFailure<T> {
  @NotNull
  CounterExample<T> getFirstCounterExample();

  @NotNull
  CounterExample<T> getMinimalCounterexample();
  
  int getTotalMinimizationStepCount();

  interface CounterExample<T> {
    T getExampleValue();

    @Nullable
    Throwable getExceptionCause();
  }
  
}
