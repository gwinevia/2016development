#!/bin/sh
# rm ~/foo -rf

# テスト用リポジトリを作る (~/foo/test )
mkdir development
cd ~/development && pwd
mkdir develop2016
cd develop2016/ && pwd
git init
touch test.txt
echo "foo" >> test.txt
git add . && git commit -m "test commit"

sep

# テスト用リモートを作る (~/foo/repo.git)
cd ~/development && pwd
mkdir develop2016.git
cd develop2016.git && pwd
git init --bare #リモートリポジトリを作るためにbareリポジトリを作る

sep

cd ~/development/develop2016 && pwd
git push ~/development/develop2016.git master

sep

# フックを設定する (~/receive)
cd ~/development && pwd
git clone ~/development/develop2016.git receive # cloneのときreceiveというディレクトリ名でクローンする

cd develop2016.git/hooks/ && pwd

{
echo "#!/bin/sh"
echo ""
echo "# . /home/mmk/development/develop2016.git/hooks/post-receive"
echo 'echo "test post-receive"'
echo "pwd"
echo "cd ~/development/receive # post-receiveが実行されるときはdevelop2016.git/にいる"
echo "pwd"
echo "git --git-dir=.git pull ../develop2016.git master"
} > post-receive

# post-receiveはchmodで実行権限を与えておかないと実行されない
chmod +x post-receive

sep

# post-receiveが動くかの確認
cd ~/development/develop2016 && pwd
echo "test" >> test.txt && git add . && git commit -m "test commit" && git push ../develop2016.git/ master