include "resources\barebones-library\String.bb";
include "resources\barebones-library\Math.bb";
include "resources\barebones-library\Test.bb";
include "resources\barebones-library\List.bb";
include "resources\barebones-library\Element.bb";
include "resources\barebones-library\Person.bb";

class TestFile2 {
  function void start() {
    // var Person Jordan = Person.make("Jordan", "Bunke", true);
    // Jordan.setGivenName("Chelsea");
    // print(Jordan.getName());
    // print(Math.sqrt(14));
    // print(Math.sqrt(121));
    // Test.assert(Math.sqrt(100) == 10, "The square root of 100 is 10.");

    print(String.to_upper("whatthehell"@7) ++ "ammy");

    var Element e1 = Element.make(10);
    var Element e2 = Element.make(2);
    var Element e3 = Element.make(8);
    var Element e4 = Element.make(4);
    var Element e5 = Element.make(6);
    e1.setNext(e2);
    e2.setNext(e3);
    e3.setNext(e4);
    e4.setNext(e5);
    var List dl = List.make(e1);
    // dl.add(e2);
    // dl.add(e3);
    // dl.add(e4);
    // dl.add(e5);
    print("List: ");
    dl.printList();
    print("List sum: ");
    print(dl.sumList());
  }
}
