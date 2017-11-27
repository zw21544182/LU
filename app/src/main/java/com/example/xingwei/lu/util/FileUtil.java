package com.example.xingwei.lu.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.xingwei.lu.modern.AudioModern;
import com.example.xingwei.lu.modern.PdfModule;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 创建时间: 2017/10/18
 * 创建人: Administrator
 * 功能描述:文件工具类
 */

public class FileUtil {


    private SimpleDateFormat dateFormat, format;
    private List<AudioModern> audioModerns;
    private Context context;
    private static FileUtil fileUtil;

    private FileUtil(Context context) {
        this.context = context;
        dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        format = new SimpleDateFormat("yyyy年MM月dd日");

    }

    public static FileUtil getInstance(Context context) {
        if (fileUtil == null) {
            fileUtil = new FileUtil(context);
        }
        return fileUtil;
    }

    public synchronized void getAudioInfoByPath(String path, final Handler handler) {
        if (audioModerns == null) {
            //如果videModerns为空
            audioModerns = new ArrayList<>();//新建集合
        }
        File file = new File(path);//通过路径新建一个文件对象
        if (!file.exists()) {//如果文件不存在
            return;//返回
        }
        final File[] audioFiles = file.listFiles();//获取file文件夹下所有的子文件
        if (audioFiles == null) {//如果viddeoFiles为空
            handler.sendEmptyMessage(0);//发送通知
            return;//返回
        }
        if (audioFiles.length == 0) {//如果audioFiles的长度为0
            handler.sendEmptyMessage(0);//发送通知
            return;//返回
        }
        new Thread() {
            @Override
            public void run() {
                //子线程
                super.run();
                //初始化集合
                setAudioData(audioFiles, handler);
            }
        }.start();


    }



    private synchronized void setAudioData(File[] audioFiles, Handler handler) {
        audioModerns.clear();
        //遍历文件
        for (File audioFile : audioFiles
                ) {
            Log.d("xwls", audioFile.getName()
            );
            AudioModern audioModern = new AudioModern();
            audioModern.setPath(audioFile.getAbsolutePath());
            audioModern.setFileName(audioFile.getName());
            if (audioFile.getName().endsWith("mp4")) {
                audioModern.setDuration(getRingDuring(audioFile.getAbsolutePath()));
            }
            audioModern.setTime("保存于 " + getTimeByName(audioFile.getPath()));
            audioModerns.add(audioModern);
        }
        Message message = new Message();
        message.obj = audioModerns;
        message.what = 1;
        handler.sendMessage(message);
    }

    private String getTimeByName(String path) {
        String time = "";
        File file = new File(path);
        time = format.format(new Date(file.lastModified()));
        return time;

    }

    public static String getRingDuring(String mUri) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(mUri);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播
        mmr.release();
        int time = Integer.parseInt(duration);
        int hour = time / 1000 / 3600;
        int minute = time / 1000 / 60;
        int second = time / 1000;
        duration = "时长: " + hour + " 时 " + minute + " 分 " + second + " 秒";
        return duration;
    }

    /**
     * 涉及到大量文件操作务必放在子线程中执行
     *
     * @param strPath 根文件路径
     */
    public void checkPdf(String strPath) {
        PdfModule pdfModule = new PdfModule();
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    checkPdf(files[i].getAbsolutePath()); // 获取文件绝对路径
                } else if (fileName.endsWith("pdf")) { // 判断文件名是否以.pdf结尾
                    String strFileName = files[i].getAbsolutePath();
                    pdfModule.setName(fileName);
                    pdfModule.setPath(strFileName);
                    pdfModule.setTime(getTimeByName(strFileName));
                    pdfModule.saveOrUpdate("path = ?", pdfModule.getPath());
                    pdfModule.clearSavedState();

                } else {
                    continue;
                }
            }
        }

    }


    public boolean isRename(String newName,String audioPath) {
        String path = audioPath;
        boolean res = false;

        File file = new File(path);
        File[] files = file.listFiles();
        for (File f : files
                ) {
            String fileName = f.getName();
            if (fileName.substring(0, fileName.length() - 4).trim().equals(newName.trim())) {
                res = true;
                break;
            }
        }
        return res;
    }
}
