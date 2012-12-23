if [ $# -ne 1 ]; then
  echo "Please provide the project root path as argument"
else
  PRJ_ROOT_PTH=$1
  CUR_PTH=$(pwd)

  cd ${PRJ_ROOT_PTH}
  mvn clean validate compile test package install site
  cd ${CUR_DIR}
fi
