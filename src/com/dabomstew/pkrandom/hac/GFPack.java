package com.dabomstew.pkrandom.hac;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GFPack {

    private static final long MAGIC = 0x4B434150584C4647L;
    private GFPackHeader header;
    private GFPackPointers pointers;
    private FileHashAbsolute[] hashAbsolute;
    private FileHashFolder[] hashInFolder;
    private FileData[] fileTable;

    private byte[][] compressedFiles;
    private byte[][] decompressedFiles;

    private LZ4FastDecompressor decompressor = LZ4Factory.fastestInstance().fastDecompressor();
    private LZ4Compressor compressor = LZ4Factory.fastestInstance().fastCompressor();

    public GFPack(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        readPack(bb);
    }

    private void readPack(ByteBuffer bb) {
        header = new GFPackHeader(bb);
        pointers = new GFPackPointers(bb, header.countFolders);

        System.out.println();
        System.out.println(pointers.ptrHashPaths == bb.position());
        hashAbsolute = new FileHashAbsolute[header.countFiles];
        for (int i = 0; i < hashAbsolute.length; i++) {
            hashAbsolute[i] = new FileHashAbsolute(bb);
        }

        hashInFolder = new FileHashFolder[header.countFolders];
        for (int i = 0; i < hashInFolder.length; i++) {
            System.out.println(pointers.ptrHashFolders[i] == bb.position());
            hashInFolder[i] = new FileHashFolder();
            hashInFolder[i].folder = new FileHashFolderInfo(bb);
            hashInFolder[i].files = new FileHashIndex[hashInFolder[i].folder.fileCount];
            for (int j = 0; j < hashInFolder[i].files.length; j++) {
                hashInFolder[i].files[j] = new FileHashIndex(bb);
            }
        }

        System.out.println(pointers.ptrFileTable == bb.position());
        fileTable = new FileData[header.countFiles];
        for (int i = 0; i < fileTable.length; i++) {
            fileTable[i] = new FileData(bb);
        }

        compressedFiles = new byte[header.countFiles][];
        for (int i = 0; i < compressedFiles.length; i++) {
            bb.position(fileTable[i].offsetPacked);
            compressedFiles[i] = new byte[fileTable[i].sizeCompressed];
            bb.get(compressedFiles[i], 0, compressedFiles[i].length);
        }

        decompressedFiles = new byte[header.countFiles][];
        for (int i = 0; i < decompressedFiles.length; i++) {
            decompressedFiles[i] = decompress(compressedFiles[i], fileTable[i].sizeDecompressed, fileTable[i].type);
        }
    }

    private byte[] writePack() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeTableHeaderList(baos);
        for (int i = 0; i < decompressedFiles.length; i++) {
            FileData entry = fileTable[i];
            byte[] decompressedFile = decompressedFiles[i];
            byte[] compressedFile = compress(decompressedFile, entry.type);
            compressedFiles[i] = compressedFile;

            entry.sizeDecompressed = decompressedFile.length;
            entry.sizeCompressed = compressedFile.length;
            entry.offsetPacked = baos.size();

            baos.write(compressedFile, 0, compressedFile.length);
            while (baos.size() % 0x10 != 0) {
                baos.write(0);
            }
        }

        byte[] pack = baos.toByteArray();
        baos.reset();
        writeTableHeaderList(baos);
        byte[] newHeaderTableList = baos.toByteArray();
        System.arraycopy(newHeaderTableList, 0, pack, 0, newHeaderTableList.length);

        return pack;
    }

    private void writeTableHeaderList(ByteArrayOutputStream baos) {
        byte[] headerBytes = header.getBytes();
        baos.write(headerBytes, 0, headerBytes.length);
        byte[] pointersBytes = pointers.getBytes();
        baos.write(pointersBytes, 0, pointersBytes.length);
        pointers.ptrHashPaths = baos.size();
        for (FileHashAbsolute hp: hashAbsolute) {
            byte[] hpBytes = hp.getBytes();
            baos.write(hpBytes, 0, hpBytes.length);
        }

        for (int i = 0; i < pointers.ptrHashFolders.length; i++) {
            pointers.ptrHashFolders[i] = baos.size();

            FileHashFolder folder = hashInFolder[i];
            folder.folder.fileCount = folder.files.length;
            byte[] folderBytes = folder.folder.getBytes();
            baos.write(folderBytes, 0, folderBytes.length);
            for (FileHashIndex hi: folder.files) {
                byte[] hashIndexBytes = hi.getBytes();
                baos.write(hashIndexBytes, 0, hashIndexBytes.length);
            }
        }

        pointers.ptrFileTable = baos.size();
        for (FileData ft: fileTable) {
            byte[] fileDataBytes = ft.getBytes();
            baos.write(fileDataBytes, 0, fileDataBytes.length);
        }
    }

    private byte[] decompress(byte[] compressedData, int decompressedLength, CompressionType type) {
        switch (type) {
            case None:
                return compressedData;
            case ZLib:
                System.err.println("Tried to decompress ZLib!");
                return compressedData;
            case LZ4:
                byte[] decompressedData = new byte[decompressedLength];
                decompressor.decompress(compressedData, decompressedData, decompressedLength);
                return decompressedData;
            case OodleHydra:
            case OodleKraken:
            case OodleLeviathan:
            case OodleMermaid:
            case OodleSelkie:
                System.err.println("Tried to decompress Oodle!");
                return compressedData;
            default:
                System.err.println("Tried to decompress invalid type!");
                return compressedData;
        }
    }

    private byte[] compress(byte[] decompressedData, CompressionType type) {
        switch (type) {
            case None:
                return decompressedData;
            case ZLib:
                System.err.println("Tried to compress with ZLib!");
                return decompressedData;
            case LZ4:
                return compressor.compress(decompressedData);
            case OodleHydra:
            case OodleKraken:
            case OodleLeviathan:
            case OodleMermaid:
            case OodleSelkie:
                System.err.println("Tried to compress with Oodle!");
                return decompressedData;
            default:
                System.err.println("Tried to compress with invalid type!");
                return decompressedData;
        }
    }

    public byte[] getDataFileName(String name) {
        int index = getIndexFileName(name);
        return decompressedFiles[index];
    }

    private int getIndexFileName(String name) {
        for (FileHashFolder hf: hashInFolder) {
            int index = hf.getIndexFileName(name);
            if (index >= 0) {
                return hf.files[index].index;
            }
        }
        return -1;
    }

    private class GFPackHeader {
        private static final int SIZE = 0x18;
        private static final long MAGIC = GFPack.MAGIC;
        private static final int VERSION = 0x1000;
        private int isRelocated;

        private int countFiles;

        private int countFolders;

        private GFPackHeader(ByteBuffer bb) {
            bb.getLong();   // Magic
            bb.getInt();    // Version
            isRelocated = bb.getInt();
            countFiles = bb.getInt();
            countFolders = bb.getInt();
        }

        private byte[] getBytes() {
            ByteBuffer bb = ByteBuffer.allocate(SIZE);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putLong(MAGIC);
            bb.putInt(VERSION);
            bb.putInt(isRelocated);
            bb.putInt(countFiles);
            bb.putInt(countFolders);
            return bb.array();
        }
    }

    private class GFPackPointers {
        private long ptrFileTable;
        private long ptrHashPaths;
        private long[] ptrHashFolders;
        private int size = 16;

        public GFPackPointers(ByteBuffer bb, int folderCount) {
            ptrFileTable = bb.getLong();
            ptrHashPaths = bb.getLong();
            ptrHashFolders = new long[folderCount];
            for (int i = 0; i < ptrHashFolders.length; i++) {
                ptrHashFolders[i] = bb.getLong();
                size += 8;
            }
        }

        public byte[] getBytes() {
            ByteBuffer bb = ByteBuffer.allocate(size);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putLong(ptrFileTable);
            bb.putLong(ptrHashPaths);
            for (long table: ptrHashFolders) {
                bb.putLong(table);
            }
            return bb.array();
        }
    }

    private class FileHashAbsolute {
        private static final int SIZE = 0x8;
        private long hashFnv1aPathFull;

        private FileHashAbsolute(ByteBuffer bb) {
            hashFnv1aPathFull = bb.getLong();
        }

        private boolean isMatch(String filename) {
            return FNVHash.hashFNV1a64(filename.getBytes()) == hashFnv1aPathFull;
        }

        private byte[] getBytes() {
            ByteBuffer bb = ByteBuffer.allocate(SIZE);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putLong(hashFnv1aPathFull);
            return bb.array();
        }
    }

    private class FileHashFolder {
        private FileHashFolderInfo folder = new FileHashFolderInfo();
        private FileHashIndex[] files;

        private int getIndexFileName(long hash) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].hashFNV1aPathFileName == hash) {
                    return i;
                }
            }
            return -1;
        }

        private int getIndexFileName(String name) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isMatch(name)) {
                    return i;
                }
            }
            return -1;
        }
    }

    private class FileHashFolderInfo {
        private static final int SIZE = 0x10;

        private long hashFNV1aPathFolderName;
        private int fileCount;
        private int padding = 0xCC;

        private FileHashFolderInfo() {

        }

        private FileHashFolderInfo(ByteBuffer bb) {
            hashFNV1aPathFolderName = bb.getLong();
            fileCount = bb.getInt();
            bb.getInt();    // Padding
        }

        private boolean isMatch(String filename) {
            return FNVHash.hashFNV1a64(filename.getBytes()) == hashFNV1aPathFolderName;
        }

        private byte[] getBytes() {
            ByteBuffer bb = ByteBuffer.allocate(SIZE);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putLong(hashFNV1aPathFolderName);
            bb.putInt(fileCount);
            bb.putInt(padding);
            return bb.array();
        }
    }

    private class FileHashIndex {
        private static final int SIZE = 0x10;

        private long hashFNV1aPathFileName;
        private int index;
        private int padding = 0xCC;

        private FileHashIndex(ByteBuffer bb) {
            hashFNV1aPathFileName = bb.getLong();
            index = bb.getInt();
            bb.getInt();    // Padding
        }

        private boolean isMatch(String filename) {
            return FNVHash.hashFNV1a64(filename.getBytes()) == hashFNV1aPathFileName;
        }

        private byte[] getBytes() {
            ByteBuffer bb = ByteBuffer.allocate(SIZE);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putLong(hashFNV1aPathFileName);
            bb.putInt(index);
            bb.putInt(padding);
            return bb.array();
        }
    }

    private class FileData {
        private static final int SIZE = 0x18;

        private short level = 9;
        private CompressionType type;
        private int sizeDecompressed;
        private int sizeCompressed;
        private int padding = 0xCC;
        private int offsetPacked;
        private int unused;

        private FileData(ByteBuffer bb) {
            bb.getShort();  // Level
            type = CompressionType.values()[bb.getShort()];
            sizeDecompressed = bb.getInt();
            sizeCompressed = bb.getInt();
            bb.getInt();    // Padding
            offsetPacked = bb.getInt();
            unused = bb.getInt();
        }

        private byte[] getBytes() {
            ByteBuffer bb = ByteBuffer.allocate(SIZE);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putShort(level);
            bb.putShort((short)type.ordinal());
            bb.putInt(sizeDecompressed);
            bb.putInt(sizeCompressed);
            bb.putInt(padding);
            bb.putInt(offsetPacked);
            bb.putInt(unused);
            return bb.array();
        }
    }

    private enum CompressionType {
        None,
        ZLib,
        LZ4,
        OodleKraken,
        OodleLeviathan,
        OodleMermaid,
        OodleSelkie,
        OodleHydra
    }
}
