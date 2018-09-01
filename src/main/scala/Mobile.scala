
import java.io.{File, IOException, RandomAccessFile}
import java.util.concurrent.locks.{ReadWriteLock, ReentrantReadWriteLock}
import java.util.regex.Pattern

import com.google.common.io.{FileWriteMode, Files}

/**
  * @author tcrow.luo
  */
class Mobile {
  private final val INIT_BUFFER_SIZE: Int = 1024 * 1024
  /**
   * 正则表达式：验证手机号
   */
  private final val REGEX_MOBILE: String = "^(1[0-9])\\d{9}$"
  /**
   * 二进制1~8位分别为1的值，与原值进行或操作即可完成在号码库的新增操作
   */
  private final val ARRAY_BYTE: Array[Byte] = Array[Byte](1,2,4,8,16,32,64,-128);

  /**
   * 二进制掩码，-1 用二进制表示 为 11111111
   * 因此任何字节异或掩码后可以获得取反值，例如 00000001 ^ 11111111 = 11111110
   */
  private final val MASK_BYTE: Byte = -1
  /**
   * 用于存储手机号码是否存在
   * 因为中国手机号码都是1开头，所以第一位省略
   * 我们需要表示最大9999999999个号码是否存在
   * 1字节 = 8 bit 最多可以表示8个号码
   * 因此需要空间 9999999999 / 8 = 1249999999.875 约等于 125 * 10 ^ 7 字节 约为 1.165 G 空间
   * 直接加载到内存中比较浪费，因此可以创建一个二进制文件直接表示，然后通过RandomAccessFile类读文件相应的位
   */
  private var dictFile: File = null
  private var file: RandomAccessFile = null
  /**
   * 读写全局锁，保证 读读共享, 读写互斥, 写写互斥
   */
  private final val LOCK: ReadWriteLock = new ReentrantReadWriteLock

  def this(filePath: String) {
    this()
    dictFile = new File(filePath)
    if (!dictFile.exists) {
      try {
        init
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
      }
    }
    file = new RandomAccessFile(dictFile, "rw")
  }

  private def init {
    LOCK.writeLock.lock
    try {
      Files.createParentDirs(dictFile)
      val loop: Int = 1250000000 / INIT_BUFFER_SIZE + 1
      val buffer = new Array[Byte](INIT_BUFFER_SIZE)
      var i: Int = 0
      while (i < loop) {
        {
          Files.asByteSink(dictFile, FileWriteMode.APPEND).write(buffer)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    finally {
      LOCK.writeLock.unlock
    }
  }

  /**
   * 新增电话号码到字典中
   *
   * @param mobile
   */
  def insert(mobile: String) {
    if (!isMobile(mobile)) {
      throwException(mobile)
    }
    if (hasMobile(mobile)) {
      return
    }

    val no: Long = mobile.toLong - 10000000000L
    val byteNum: Int = (no / 8).asInstanceOf[Int]
    val bit: Int = (no % 8).asInstanceOf[Int]
    var b: Byte = read(byteNum)
    b = (b | ARRAY_BYTE(bit)).asInstanceOf[Byte]
    write(byteNum, b)
  }

  /**
   * 从字典中删除电话号码
   * @param mobile
   * @throws IOException
   */
  def delete(mobile: String) {
    if (!isMobile(mobile)) {
      throwException(mobile)
    }
    if (!hasMobile(mobile)) {
      return
    }
    val no: Long = mobile.toLong - 10000000000L
    val byteNum: Int = (no / 8).asInstanceOf[Int]
    val bit: Int = (no % 8).asInstanceOf[Int]
    var b: Byte = read(byteNum)
    b = (b & (ARRAY_BYTE(bit) ^ MASK_BYTE)).asInstanceOf[Byte]
    write(byteNum, b)
  }

  private def throwException(mobile: String) {
    throw new RuntimeException("The string \"" + mobile + "\" is not the mobile number.")
  }

  private def throwUnknownException {
    throw new RuntimeException("read data unknown exception")
  }

  def hasMobile(mobile: String): Boolean = {
    if (!isMobile(mobile)) {
      throwException(mobile)
    }
    val no: Long = mobile.toLong - 10000000000L
    val byteNum: Int = (no / 8).asInstanceOf[Int]
    val bit: Int = (no % 8).asInstanceOf[Int]
    val b: Byte = read(byteNum)
    if (ARRAY_BYTE(bit) == (b & ARRAY_BYTE(bit)).asInstanceOf[Byte]) {
      return true
    }
    else {
      return false
    }
  }

  private def isMobile(mobile: String): Boolean = {
    return Pattern.matches(REGEX_MOBILE, mobile)
  }

  private def read(byteNum: Int): Byte = {
    LOCK.readLock.lock
    val buffer: Array[Byte] = new Array[Byte](1)
    try {
      file.seek(byteNum)
      val read: Int = file.read(buffer)
      if (read <= 0) {
        throwUnknownException
      }
    }
    finally {
      LOCK.readLock.unlock
    }
    return buffer(0)
  }

  private def write(byteNum: Int, b: Byte) {
    LOCK.writeLock.lock
    try {
      file.seek(byteNum)
      val buffer: Array[Byte] = new Array[Byte](1)
      buffer(0) = b
      file.write(buffer)
    }
    finally {
      LOCK.writeLock.unlock
    }
  }

}
