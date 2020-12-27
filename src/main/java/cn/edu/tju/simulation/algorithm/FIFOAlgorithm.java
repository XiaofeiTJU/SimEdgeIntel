package cn.edu.tju.simulation.algorithm;

import cn.edu.tju.simulation.IF.files.CachingSingleContent;
import cn.edu.tju.simulation.IF.files.SingleLocalHobby;
import cn.edu.tju.simulation.context.Context;
import cn.edu.tju.simulation.IF.edgenode.WirelessNetwork;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FIFOAlgorithm implements RealTimeAlgorithm {

    public void setCache(WirelessNetwork network, SingleLocalHobby requestContent) {
        long remainingSize = network.getRemainingCacheSize();
        LinkedList<CachingSingleContent> cachingContentList = network.getCacheContent();
        CachingSingleContent includeContent = include(cachingContentList,requestContent);

        if (includeContent != null) {
            cachingContentList.remove(includeContent);
            cachingContentList.addLast(includeContent);

        } else if (network.getCacheSize() >= requestContent.getSize()) {

            if (remainingSize >= requestContent.getSize()) {
                cachingContentList.addLast(new CachingSingleContent(requestContent.getSingleContent()));
            } else {
                while(cachingContentList.size()!=0){
                    remainingSize += cachingContentList.getFirst().getSize();
                    cachingContentList.removeFirst();
                    if(remainingSize >= requestContent.getSize()){
                        cachingContentList.addFirst(new CachingSingleContent(requestContent.getSingleContent()));
                        break;
                    }
                }
            }



        }
        Context.getInstance().appendLog("debug", "FIFO is Updating Cache", null);
    }

    public CachingSingleContent include(List<CachingSingleContent> cacheContent, SingleLocalHobby content) {
        Iterator<CachingSingleContent> it = cacheContent.iterator();
        while (it.hasNext()) {
            CachingSingleContent csc = it.next();
            if (csc.getSingleContent() == content.getSingleContent()) {
                return csc;
            }
        }
        return null;
    }

}
