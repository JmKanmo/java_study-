import io.netty.buffer.*;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class ByteBufTest {
    @Test
    public void testBuffer1() {
        ByteBuf heapByteBuf = Unpooled.buffer();
        ByteBuf nonHeapByteBuf = Unpooled.directBuffer();
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();

        Assert.assertTrue(heapByteBuf.hasArray());
        Assert.assertFalse(nonHeapByteBuf.hasArray());

        compositeByteBuf.addComponents(heapByteBuf, nonHeapByteBuf);

        // heapByteBuf.readByte(); // intexOutOfBounds exception 발생

        heapByteBuf.writeByte((byte) 25);
        heapByteBuf.writeByte((byte) 100);
        Assert.assertEquals(heapByteBuf.writerIndex(), 2);

        byte b = heapByteBuf.readByte();
        Assert.assertEquals(b, (byte) 25);
        b = heapByteBuf.readByte();
        Assert.assertEquals(b, (byte) 100);
        Assert.assertEquals(heapByteBuf.readerIndex(), 2);

        nonHeapByteBuf.writeInt(55);
        nonHeapByteBuf.writeInt(551);
        nonHeapByteBuf.writeInt(553);

        ByteBuf temp = Unpooled.directBuffer(nonHeapByteBuf.readableBytes());
        int readableSize = nonHeapByteBuf.readableBytes();
        nonHeapByteBuf.readBytes(temp);
        Assert.assertEquals(nonHeapByteBuf.readableBytes(), 0);
        Assert.assertEquals(nonHeapByteBuf.readerIndex(), nonHeapByteBuf.writerIndex());
        Assert.assertEquals(temp.writerIndex() - temp.readerIndex(), temp.readableBytes());
        Assert.assertEquals(temp.readableBytes(), readableSize);

        temp.readInt();
        temp.readInt();
        temp.readInt();
        Assert.assertEquals(temp.readableBytes(), 0);

    }

    @Test
    public void compositeByteBufferTest() {
        ByteBuf byteBuf1 = Unpooled.buffer(100);

        byteBuf1.writeInt(255);
        byteBuf1.writeByte((byte) 25);
        byteBuf1.writeLong(44334);

        ByteBuf byteBuf2 = Unpooled.buffer(100);

        byteBuf1.getBytes(0, byteBuf2);

        byteBuf2.setInt(0, 5555);

        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
        compositeByteBuf.addComponents(byteBuf1, byteBuf2);

        ByteBuf temp = Unpooled.buffer(100);
        ByteBuf dst = compositeByteBuf.component(0);
        dst.getBytes(0, temp, dst.readableBytes());
        System.out.println(temp.readInt() + " " + temp.readByte() + " " + temp.readLong());
        temp.markReaderIndex();
        temp.writeLong(999);
        temp.markWriterIndex();
        temp.writeChar('h');
        System.out.println(temp.readLong() + " " + temp.readChar());
        temp.resetReaderIndex();
        System.out.println(temp.readLong() + " " + temp.readChar());
        temp.resetReaderIndex();
        temp.resetWriterIndex();
        System.out.println(temp.readerIndex() + " " + temp.writerIndex());
        temp.writeChar('j');
        System.out.println(temp.readLong() + " " + temp.readChar());
    }

    @Test
    public void testBuffer2() {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(500);
        byteBuf.writeShort((short) 255);
        byteBuf.writeBytes(new byte[]{(byte) 100, (byte) 200, (byte) 125});

        byteBuf.markReaderIndex();

        int iVar = byteBuf.readInt();
        short sVar = byteBuf.readShort();
        byte[] bArr = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bArr);
        Assert.assertEquals(byteBuf.readableBytes(), 0);
        byteBuf.resetReaderIndex();
        Assert.assertEquals(byteBuf.readableBytes(), 9);

        byteBuf.readerIndex(5);
        Assert.assertEquals(byteBuf.readerIndex(), 5);
        byteBuf.resetReaderIndex();
        Assert.assertEquals(byteBuf.readerIndex(), 0);
    }

    @Test
    public void testBuffer3() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello guys", CharsetUtil.UTF_8);
        ByteBuf copiedByteBuf = byteBuf.copy();
        ByteBuf slicedByteBuf = byteBuf.slice();

//        copiedByteBuf.setByte(0,'n');
//        Assert.assertTrue(byteBuf.toString(CharsetUtil.UTF_8).equals(copiedByteBuf.toString(CharsetUtil.UTF_8)));

        slicedByteBuf.setByte(0, 'n');
        Assert.assertTrue(byteBuf.toString(CharsetUtil.UTF_8).equals(slicedByteBuf.toString(CharsetUtil.UTF_8)));
        Assert.assertFalse(slicedByteBuf.toString(CharsetUtil.UTF_8).equals(copiedByteBuf.toString(CharsetUtil.UTF_8)));

        copiedByteBuf.setByte(3, 'h');
        Assert.assertEquals(copiedByteBuf.readerIndex(), 0);
        copiedByteBuf.readByte();
        copiedByteBuf.readByte();

        byte[] bytes = new byte[copiedByteBuf.readableBytes()];
        copiedByteBuf.getBytes(0, bytes);
        String str = new String(bytes, CharsetUtil.UTF_8);
        System.out.println(str);
    }

    @Test
    public void testBuffer5() {
        ByteBuf byteBuf = Unpooled.directBuffer();
        System.out.println(byteBuf.refCnt());
        byteBuf.writeInt(25);
        ByteBuf temp = byteBuf.copy();
        temp.writeInt(25);
        System.out.println(byteBuf.refCnt());
        temp.setInt(0, 30);
        boolean ret = ByteBufUtil.equals(byteBuf, temp);
        System.out.println(ret);
        byteBuf.release();
        System.out.println(byteBuf.refCnt());
    }
}
