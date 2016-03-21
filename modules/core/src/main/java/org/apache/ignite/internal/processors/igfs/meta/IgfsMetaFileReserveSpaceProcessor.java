package org.apache.ignite.internal.processors.igfs.meta;

import org.apache.ignite.binary.BinaryObjectException;
import org.apache.ignite.binary.BinaryRawReader;
import org.apache.ignite.binary.BinaryRawWriter;
import org.apache.ignite.binary.BinaryReader;
import org.apache.ignite.binary.BinaryWriter;
import org.apache.ignite.binary.Binarylizable;
import org.apache.ignite.internal.processors.igfs.IgfsEntryInfo;
import org.apache.ignite.internal.processors.igfs.IgfsFileAffinityRange;
import org.apache.ignite.internal.processors.igfs.IgfsFileMap;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.lang.IgniteUuid;

import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * File reserve space entry processor.
 */
public class IgfsMetaFileReserveSpaceProcessor implements EntryProcessor<IgniteUuid, IgfsEntryInfo, IgfsEntryInfo>,
    Externalizable, Binarylizable {
    /** */
    private static final long serialVersionUID = 0L;

    /** Space. */
    private long space;

    /** Affinity range. */
    private IgfsFileAffinityRange affRange;

    /**
     * Default constructor.
     */
    public IgfsMetaFileReserveSpaceProcessor() {
        // No-op.
    }

    /**
     * Constructor.
     *
     * @param space Space.
     * @param affRange Affinity range.
     */
    public IgfsMetaFileReserveSpaceProcessor(long space, IgfsFileAffinityRange affRange) {
        this.space = space;
        this.affRange = affRange;
    }

    /** {@inheritDoc} */
    @Override public IgfsEntryInfo process(MutableEntry<IgniteUuid, IgfsEntryInfo> entry, Object... args)
        throws EntryProcessorException {
        IgfsEntryInfo oldInfo = entry.getValue();

        IgfsFileMap newMap = new IgfsFileMap(oldInfo.fileMap());

        newMap.addRange(affRange);

        IgfsEntryInfo newInfo = oldInfo.length(oldInfo.length() + space).fileMap(newMap);

        entry.setValue(newInfo);

        return newInfo;
    }

    /** {@inheritDoc} */
    @Override public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(space);
        out.writeObject(affRange);
    }

    /** {@inheritDoc} */
    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        space = in.readLong();
        affRange = (IgfsFileAffinityRange)in.readObject();
    }

    /** {@inheritDoc} */
    @Override public void writeBinary(BinaryWriter writer) throws BinaryObjectException {
        BinaryRawWriter out = writer.rawWriter();

        out.writeLong(space);
        out.writeObject(affRange);
    }

    /** {@inheritDoc} */
    @Override public void readBinary(BinaryReader reader) throws BinaryObjectException {
        BinaryRawReader in = reader.rawReader();

        space = in.readLong();
        affRange = in.readObject();
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(IgfsMetaFileReserveSpaceProcessor.class, this);
    }
}
