var gulp = require('gulp'),
    concat = require('gulp-concat');

gulp.task('editsite', function() {
  return gulp.src('./runtime/assets/private/editsite/*.js')
    .pipe(concat('editsite.js'))
    .pipe(gulp.dest('./runtime/assets/private/js'));
});

gulp.task('site', function() {
  return gulp.src('./runtime/assets/public/site/*.js')
    .pipe(concat('site.js'))
    .pipe(gulp.dest('./runtime/assets/public/js'));
});

gulp.task('map', function() {
  return gulp.src('./runtime/assets/public/map/*.js')
    .pipe(concat('map.js'))
    .pipe(gulp.dest('./runtime/assets/public/js'));
});

gulp.task('search', function() {
  return gulp.src('./runtime/assets/public/search/*.js')
    .pipe(concat('search.js'))
    .pipe(gulp.dest('./runtime/assets/public/js'));
});

gulp.task('routing', function() {
  return gulp.src('./runtime/assets/public/routing/*.js')
    .pipe(concat('routing.js'))
    .pipe(gulp.dest('./runtime/assets/public/js'));
});


gulp.task('default', ['editsite','search','map','site','routing']);
