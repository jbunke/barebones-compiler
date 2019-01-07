include "resources\barebones-library\String.bb";

class IndexOfTest {
  function void start() {
    print("'es' in 'test': " ++ String.indexOf("es", "test"));
    print("'standing' in 'understanding': " ++ String.indexOf("standing", "understanding"));
    print("'under' in 'understanding': " ++ String.indexOf("under", "understanding"));
    print("'lion' in 'understanding': " ++ String.indexOf("lion", "understanding"));
  }
}