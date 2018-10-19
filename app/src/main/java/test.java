import com.hyphenate.easeui.utils.PathUtils;
import com.stratagile.pnrouter.utils.FileUtil;

import java.io.File;

public class test {

    public static void main(String args[])
    {
        String files_dir_amr = "C://20181018T193748.amr";
        File filesFileAmr = new File(files_dir_amr);
        boolean is = filesFileAmr.exists();
        int longTime = FileUtil.getAmrDuration(filesFileAmr);
        int aa = 0;
    }
}
