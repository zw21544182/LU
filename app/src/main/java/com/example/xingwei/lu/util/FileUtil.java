package com.example.xingwei.lu.util;

import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.xingwei.lu.modern.ImageModern;
import com.example.xingwei.lu.modern.PdfModule;
import com.example.xingwei.lu.modern.VideoModern;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * 创建时间: 2017/10/18
 * 创建人: Administrator
 * 功能描述:文件工具类
 */

public class FileUtil {
    public static final int IMAGE = 44;
    public static final int VIDEO = 2;
    public static final int MOVIE = 3;
    List<VideoModern> videoModerns;
    List<VideoModern> movieModerns;
    List<ImageModern> imageModerns;
    SimpleDateFormat dateFormat, format;

    public FileUtil() {
        dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        format = new SimpleDateFormat("yyyy年MM月dd日");
        movieModerns = new ArrayList<>();

    }


    public void getVideoInfoByPath(String path, final Handler handler) {
        if (videoModerns == null) {
            //如果videModerns为空
            videoModerns = new ArrayList<>();//新建集合
        }
        videoModerns.clear();//清除集合中所有的元素
        File file = new File(path);//通过路径新建一个文件对象

        if (!file.exists()) {//如果文件不存在
            return;//返回
        }
        final File[] videoFiles = file.listFiles();//获取file文件夹下所有的子文件
        if (videoFiles == null) {//如果viddeoFiles为空
            handler.sendEmptyMessage(0);//发送通知
            return;//返回
        }
        if (videoFiles.length == 0) {//如果videoFiles的长度为0
            handler.sendEmptyMessage(0);//发送通知
            return;//返回
        }
        new Thread() {
            @Override
            public void run() {
                //子线程
                super.run();
                //遍历文件
                for (File videoFile : videoFiles
                        ) {
                    Message message = new Message();

                    VideoModern videoModern = new VideoModern();
                    videoModern.setPath(videoFile.getAbsolutePath());
                    videoModern.setFileName(videoFile.getName());
                    videoModern.setDuration(getRingDuring(videoFile.getAbsolutePath()));
                    videoModern.setTime("保存于 " + getTimeByName(videoFile.getPath()));
                    message.obj = videoModern;
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        }.start();


    }

    /**
     * @param path Environment.getExternalStorageDirectory().getAbsolutePath()+"/LU/Video"
     * @return
     */
    public List<VideoModern> getVideoInfoByPath(String path) {
        if (videoModerns == null) {
            videoModerns = new ArrayList<>();
        }
        videoModerns.clear();
        File file = new File(path);
        if (!file.exists()) {
            return videoModerns;
        }
        File[] videoFiles = file.listFiles();
        if (videoFiles.length == 0) {
            return videoModerns;
        }
        for (File videoFile : videoFiles
                ) {
            VideoModern videoModern = new VideoModern();
            videoModern.setPath(videoFile.getAbsolutePath());
            videoModern.setFileName(videoFile.getName());
            videoModern.setDuration(getRingDuring(videoFile.getAbsolutePath()));
            videoModern.setTime("保存于 " + getTimeByName(videoFile.getPath()));
            Log.d("xwl", videoModern.toString());
            videoModerns.add(videoModern);
        }
        return videoModerns;
    }

    /**
     * @param path Environment.getExternalStorageDirectory().getAbsolutePath()+"/LU/Pictures"
     * @return
     */
    public List<ImageModern> getImageInfoByPath(String path) {
        if (imageModerns == null) {
            imageModerns = new ArrayList<>();
        }
        imageModerns.clear();
        File file = new File(path);
        if (!file.exists()) {
            return imageModerns;
        }
        File[] imageFiles = file.listFiles();
        if (imageFiles.length == 0) {
            return imageModerns;
        }
        for (File imageFile : imageFiles
                ) {
            ImageModern imageModern = new ImageModern();
            imageModern.setPath(imageFile.getAbsolutePath());
            imageModern.setFileName(imageFile.getName());
            imageModern.setTime("保存于 " + getTimeByName(imageFile.getPath()));
            imageModerns.add(imageModern);
        }
        return imageModerns;
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


    public boolean isRename(String newName, int type) {
        String path = "";
        boolean res = false;
        if (type == IMAGE) {
            path = getExternalStorageDirectory().getAbsolutePath() + "/LU/Pictures";
        } else if (type == VIDEO) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LU/Video";
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LU/Movie";

        }
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
