interface Array<T> {
  // https://github.com/tc39/proposal-array-last
  /** @deprecated use `.at(-1)` */
  lastItem: T;
  /** @deprecated use `.length-1` */
  readonly lastIndex: number;

  // https://github.com/tc39/proposal-relative-indexing-method
  at(index: number): T;
}
