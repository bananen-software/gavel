import {delay, MonoTypeOperatorFunction} from "rxjs";

export function randomizedDelay<T>(): MonoTypeOperatorFunction<T> {
  return delay(Math.random() * (3000 - 500) + 500);
}
