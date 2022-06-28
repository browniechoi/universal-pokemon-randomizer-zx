// automatically generated by the FlatBuffers compiler, do not modify

package com.dabomstew.pkrandom.generated.swsh;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class SwShWildEncounterSlot extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static SwShWildEncounterSlot getRootAsSwShWildEncounterSlot(ByteBuffer _bb) { return getRootAsSwShWildEncounterSlot(_bb, new SwShWildEncounterSlot()); }
  public static SwShWildEncounterSlot getRootAsSwShWildEncounterSlot(ByteBuffer _bb, SwShWildEncounterSlot obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public SwShWildEncounterSlot __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public int probability() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public int species() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int form() { int o = __offset(8); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }

  public static int createSwShWildEncounterSlot(FlatBufferBuilder builder,
      int probability,
      int species,
      int form) {
    builder.startTable(3);
    SwShWildEncounterSlot.addSpecies(builder, species);
    SwShWildEncounterSlot.addForm(builder, form);
    SwShWildEncounterSlot.addProbability(builder, probability);
    return SwShWildEncounterSlot.endSwShWildEncounterSlot(builder);
  }

  public static void startSwShWildEncounterSlot(FlatBufferBuilder builder) { builder.startTable(3); }
  public static void addProbability(FlatBufferBuilder builder, int probability) { builder.addByte(0, (byte)probability, (byte)0); }
  public static void addSpecies(FlatBufferBuilder builder, int species) { builder.addInt(1, species, 0); }
  public static void addForm(FlatBufferBuilder builder, int form) { builder.addByte(2, (byte)form, (byte)0); }
  public static int endSwShWildEncounterSlot(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public SwShWildEncounterSlot get(int j) { return get(new SwShWildEncounterSlot(), j); }
    public SwShWildEncounterSlot get(SwShWildEncounterSlot obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

