package com.foresee.xdeploy.framework.iterator;

public abstract class ObjectIterator  implements IObjectIterator{
    protected int nextIndex = -1;
    protected  Object object;
    protected Object currentElement;
    
    public ObjectIterator() {
        // TODO Auto-generated constructor stub
    }

    public abstract int getLength() ;

    public abstract Object getCurrentObject() ;

    @Override
    public boolean hasNext() {
        return nextIndex + 1 < getLength();
    }


    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public Object next() {
        nextObj();
        return getValue();
    }

    @Override
    public boolean nextObj() {
        if (!hasNext()) {
            return false;
        }
 
        nextIndex++;
        currentElement = getCurrentObject();
        return true;
    }

    @Override
    public Object getValue() {
        return currentElement;
    }
    
	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
    

}
