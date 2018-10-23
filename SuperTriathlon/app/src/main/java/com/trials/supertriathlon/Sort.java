package com.trials.supertriathlon;


/**
 * Created by Kohei Moroi on 10/20/2016.
 */
public class Sort {

    public static void insertionSort(int sortArray[]) {
        if (sortArray == null || sortArray.length <= 0) return;
        int dummy;
        int j;
        for (int i = 1; i < sortArray.length; i++) {
            dummy = sortArray[i];
            for (j = i;;j--) {
                if (j == 0){
                    break;
                } else if (sortArray[j - 1] < dummy) {
                    break;
                }
                if (dummy < sortArray[j - 1]) {
                    sortArray[j] = sortArray[j - 1];
                }
            }
            sortArray[j] = dummy;
        }
    }
    public static void insertionSortWithAttribute(
            int sortArray[], int idList[], 
            String nameList[], int previewRank[],
            int latestRank[]
    ) {
        if (sortArray == null || sortArray.length <= 0) return;
        int sortDummy;
        int idDummy;
        String nameDummy;
        int previewDummy;
        int latestDummy;
        int j;
        for (int i = 1; i < sortArray.length; i++) {
            sortDummy = sortArray[i];
            idDummy = idList[i];
            nameDummy = nameList[i];
            previewDummy = previewRank[i];
            latestDummy = latestRank[i];
            for (j = i;;j--) {
                if (j == 0){
                    break;
                } else if (sortArray[j - 1] <= sortDummy) {
                    break;
                }
                if (sortDummy <= sortArray[j - 1]) {
                    sortArray[j] = sortArray[j - 1];
                    idList[j] = idList[j-1];
                    nameList[j] = nameList[j-1];
                    previewRank[j] = previewRank[j-1];
                    latestRank[j] = latestRank[j-1];
                }
            }
            sortArray[j] = sortDummy;
            idList[j] = idDummy;
            nameList[j] = nameDummy;
            previewRank[j] = previewDummy;
            latestRank[j] = latestDummy;
        }
    }
    public static void bubbleSort(int sortArray[]) {
        int dummy = 0;
        for(int j = 0;j < sortArray.length;j++){
            for(int i = 1;i < sortArray.length;i++){
                if(dummy == sortArray[i]){
                    sortArray[j] = dummy;
                }
                if (sortArray[j] <= sortArray[i]) {
                    dummy = sortArray[i];
                }
            }
            sortArray[j] = dummy;
        }
    }
}
