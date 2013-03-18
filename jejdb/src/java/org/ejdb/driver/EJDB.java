package org.ejdb.driver;

/**
 * @author Tyutyunkov Vyacheslav (tve@softmotions.com)
 * @version $Id$
 */
public class EJDB {
    // Open modes
    public static final int JBOREADER = 1 << 0; /**< Open as a reader. */
    public static final int JBOWRITER = 1 << 1; /**< Open as a writer. */
    public static final int JBOCREAT = 1 << 2; /**< Create if db file not exists. */
    public static final int JBOTRUNC = 1 << 3; /**< Truncate db on open. */
    public static final int JBONOLCK = 1 << 4; /**< Open without locking. */
    public static final int JBOLCKNB = 1 << 5; /**< Lock without blocking. */
    public static final int JBOTSYNC = 1 << 6; /**< Synchronize every transaction. */

    public static final int JBO_DEFAULT = (JBOWRITER | JBOCREAT | JBOTSYNC);

    static {
        System.loadLibrary("jejdb");
    }

    protected native void openDB(String path, int mode);
    protected native boolean isOpenDB();
    protected native void closeDB();
    protected native void syncDB();

    private long dbPointer;


    // TODO: move to driver class
    public void open(String path) {
        this.open(path, JBO_DEFAULT);
    }

    public void open(String path, int mode) {
        this.openDB(path, mode);
    }

    public boolean isOpen() {
        return this.isOpenDB();
    }

    public void close() {
        this.closeDB();
    }

    public void sync() {
        this.syncDB();
    }


    public EJDBCollection ensureCollection(String cname) {
        return this.ensureCollection(cname, null);
    }

    public EJDBCollection ensureCollection(String cname, EJDBCollection.Options opts) {
        if (!this.isOpen()) {
//            todo
            throw new RuntimeException("Connection does not exists");
        }

        EJDBCollection collection = getCollection(cname);
        collection.ensureExists(opts);

        return collection;
    }

    public void dropCollection(String cname) {
        this.dropCollection(cname, false);
    }

    public void dropCollection(String cname, boolean prune) {
        if (!this.isOpen()) {
            // todo
            throw new RuntimeException("Connection does not exists");
        }

        EJDBCollection collection = getCollection(cname);

        collection.drop(prune);
    }

    public EJDBCollection getCollection(String cname) {
        return this.getCollection(cname, false);
    }

    public EJDBCollection getCollection(String cname, boolean ecreate) {
        return this.getCollection(cname, ecreate, null);
    }

    public EJDBCollection getCollection(String cname, boolean ecreate, EJDBCollection.Options opts) {
        EJDBCollection collection = new EJDBCollection(this, cname);

        if (ecreate) {
            collection.ensureExists(opts);
        }

        return collection;
    }

    //////////////////////////////////////////
    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}