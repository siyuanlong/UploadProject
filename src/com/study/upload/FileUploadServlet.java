package com.study.upload;

import com.study.util.UUIDUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

@WebServlet("/fileUploadServlet")
public class FileUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //构建文件存储位置,获取存储位置的服务器路径
        String savePath = getServletContext().getRealPath("/savePath");
        //构建File对象
        File saveFile = new File(savePath);
        //判断文件夹是否存在，如果不存在，则新建文件夹
        if (!saveFile.exists()){
            saveFile.mkdirs();
        }
        //构建临时文件存储位置
        String tempPath = getServletContext().getRealPath("/tempPath");
        //构建File对象
        File tempFile = new File(tempPath);
        //判断临时存储文件夹是否存在，如果不存在，则新建文件夹
        if (!tempFile.exists()){
            tempFile.mkdirs();
        }
        //构建DiskFileItemFactory对象
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

        //设置何时开启临时文件夹
        diskFileItemFactory.setSizeThreshold(diskFileItemFactory.getSizeThreshold()*100);

        //设置临时文件夹
        diskFileItemFactory.setRepository(tempFile);

        //构建ServletFileUpload对象
        ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

        //监听上传进度
        servletFileUpload.setProgressListener(new ProgressListener() {
            @Override
            public void update(long l, long l1, int i) {
                //l代表读取了多少字节，ll代表当前文件总大小
                System.out.println("当前读取的字节数："+l+",当前读取的文件总大小:"+l1);
            }
        });
        //请求类型判断和格式化
        //ServletFileUpload.isMultipartContent(req)对应jsp里面的enctype="multipart/form-data"
        //判断客户端<form>标记的enctype属性是否是“multipart/form-data"。
        if (ServletFileUpload.isMultipartContent(req)){
            List<FileItem> fileItems = null;
            try {
                fileItems = servletFileUpload.parseRequest(req);
            } catch (FileUploadException e) {
                e.printStackTrace();
            }

            //控制上传文件单个大小
            servletFileUpload.setFileSizeMax(1024*1024*10);

            //控制上传文件总大小
            servletFileUpload.setSizeMax(1024*1024*1024);

            //获取迭代器
            Iterator<FileItem> iterator = fileItems.iterator();

            servletFileUpload.setHeaderEncoding("utf-8");
            while (iterator.hasNext()){
                //获取当前文件项
                FileItem fileItem = iterator.next();
                System.out.println("hello");
                //判断文件项类型
                if(fileItem.isFormField()){
                    //如果是文本类型,直接打印文本属性的名称和值
                    System.out.println(fileItem.getFieldName()+" "+fileItem.getString("utf-8"));
                }
                else{
                    //获取文件名
                    String pathName = fileItem.getName();

                    //解决乱码问题
                    pathName = new String(pathName.getBytes("GBK"), "utf-8");

                    //打印
                    System.out.println(pathName);

                    //截取文件路径
                    String fileName = pathName.substring(pathName.lastIndexOf("/") + 1);

                    //拼接文件名和UUID
                    String finalFilePath = savePath + "/" + UUIDUtil.getUUID() + fileName;

                    //构建文件输入流
                    InputStream fis = fileItem.getInputStream();
                    //获取文件输出流
                    FileOutputStream fos = new FileOutputStream(new File(finalFilePath));

                    int count = 0 ;
                    byte[] bytes = new byte[1024];
                    while((count = fis.read(bytes))!=-1){
                        fos.write(bytes,0,count);
                    }
                    fos.flush();
                    if (tempFile.exists()){
                        tempFile.delete();
                    }
                }
            }
        }
    }
}
