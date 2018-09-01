import org.scalatest.FunSuite

/**
  * @author tcrow.luo
  */
class TestMobile extends FunSuite{

  private val mobileFilePath = "D://mobile_phone/mobile_phone"

  test("Mobile") {
    var mobile = new Mobile(mobileFilePath);
    mobile.delete("13312341232")
    mobile.delete("13312341233")
    mobile.delete("13312341234")
    mobile.delete("13312341235")
    mobile.delete("13312341236")
    mobile.delete("13312341237")
    mobile.delete("13312341238")
    mobile.delete("13312341239")
    assert(!mobile.hasMobile("13312341232"))
    assert(!mobile.hasMobile("13312341233"))
    assert(!mobile.hasMobile("13312341234"))
    assert(!mobile.hasMobile("13312341235"))
    assert(!mobile.hasMobile("13312341236"))
    assert(!mobile.hasMobile("13312341237"))
    assert(!mobile.hasMobile("13312341238"))
    assert(!mobile.hasMobile("13312341239"))
    mobile.insert("13312341232")
    mobile.insert("13312341233")
    mobile.insert("13312341234")
    mobile.insert("13312341235")
    mobile.insert("13312341236")
    mobile.insert("13312341237")
    mobile.insert("13312341238")
    mobile.insert("13312341239")
    assert(mobile.hasMobile("13312341232"))
    assert(mobile.hasMobile("13312341233"))
    assert(mobile.hasMobile("13312341234"))
    assert(mobile.hasMobile("13312341235"))
    assert(mobile.hasMobile("13312341236"))
    assert(mobile.hasMobile("13312341237"))
    assert(mobile.hasMobile("13312341238"))
    assert(mobile.hasMobile("13312341239"))
    mobile.delete("13312341232")
    mobile.delete("13312341233")
    mobile.delete("13312341234")
    mobile.delete("13312341235")
    mobile.delete("13312341236")
    mobile.delete("13312341237")
    mobile.delete("13312341238")
    mobile.delete("13312341239")
    assert(!mobile.hasMobile("13312341232"))
    assert(!mobile.hasMobile("13312341233"))
    assert(!mobile.hasMobile("13312341234"))
    assert(!mobile.hasMobile("13312341235"))
    assert(!mobile.hasMobile("13312341236"))
    assert(!mobile.hasMobile("13312341237"))
    assert(!mobile.hasMobile("13312341238"))
    assert(!mobile.hasMobile("13312341239"))
  }


}
