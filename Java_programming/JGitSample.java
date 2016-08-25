import java.io.File;
import java.io.IOException;
import java.util.*;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

public class JGitSample {

	static String localPath = "/home/mmk/krswmmk";

	public static void main(String[] args) throws Exception {

        // ローカルリポジトリの指定など
        FileRepository localRepo = new FileRepository(localPath + "/.git");

        // Git オブジェクト作成 (このオブジェクトを操作していろいろする)
        Git git = new Git(localRepo);

        String actionName = args[0];

        if(actionName.equals("addcommit")){
            GitAddCommit(git);
        }else if(actionName.equals("push")){
            GitPush(git);
        }

	}

    public static void GitAddCommit(Git git) throws Exception{
        try{
            // git add
            git.add().addFilepattern("Java_programming").call();
    
            // git commit
            git.commit().setMessage("JGitSample Test").call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void GitPush(Git git) throws Exception{
        try{
            // git push
            git.push().call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}